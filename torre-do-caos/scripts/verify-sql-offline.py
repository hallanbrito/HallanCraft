#!/usr/bin/env python3
"""Offline, stdlib-only SQL smoke tests. These do NOT execute Java/JUnit/Paper."""
import pathlib
import sqlite3
import tempfile
import uuid

root = pathlib.Path(__file__).resolve().parents[1]
migration = root / 'src/main/resources/db/migration/V001__baseline.sql'
query = migration.read_text(encoding='utf-8')
with tempfile.TemporaryDirectory() as temp:
    path = str(pathlib.Path(temp) / 'test.db')
    con = sqlite3.connect(path)
    con.execute('PRAGMA foreign_keys=ON')
    con.executescript(query)
    tables = {row[0] for row in con.execute("SELECT name FROM sqlite_master WHERE type='table'")}
    required = {'player_profiles', 'player_permanent_upgrades', 'active_run_state',
                'active_run_players', 'arena_block_journal', 'pending_player_cleanups'}
    assert required.issubset(tables), required - tables
    print('PASS 1/6: six expected SQLite tables')
    p = str(uuid.uuid4())
    con.execute('INSERT INTO player_profiles(player_uuid,player_name,updated_at) VALUES (?,?,?)', (p,'P1',1))
    con.execute('UPDATE player_profiles SET permanent_coins=permanent_coins+15 WHERE player_uuid=?',(p,))
    con.commit()
    con.close()
    con = sqlite3.connect(path)
    con.execute('PRAGMA foreign_keys=ON')
    assert con.execute('SELECT permanent_coins FROM player_profiles WHERE player_uuid=?',(p,)).fetchone()[0] == 15
    print('PASS 2/6: permanent progression survives reconnect')
    con.execute("INSERT INTO arena_block_journal (session_id,world_id,arena_id,x,y,z,original_block_data) VALUES ('r1','w1','a1',2,64,3,'minecraft:stone_bricks')")
    con.execute("INSERT OR IGNORE INTO arena_block_journal (session_id,world_id,arena_id,x,y,z,original_block_data) VALUES ('r1','w1','a1',2,64,3,'minecraft:dirt')")
    assert con.execute('SELECT original_block_data FROM arena_block_journal').fetchone()[0] == 'minecraft:stone_bricks'
    print('PASS 3/6: first original block is not overwritten')
    con.execute("INSERT INTO active_run_state(session_id,slot,state,current_world_id,current_arena_id,current_floor,current_wave,started_at) VALUES('s1',1,'WAVE_COMBAT','w1','a1',1,1,1)")
    try:
        con.execute("INSERT INTO active_run_state(session_id,slot,state,current_world_id,current_arena_id,current_floor,current_wave,started_at) VALUES('s2',1,'WAVE_COMBAT','w1','a1',1,1,1)")
    except sqlite3.IntegrityError:
        print('PASS 4/6: one active run enforced')
    else:
        raise AssertionError('second active run incorrectly accepted')
    try:
        con.execute("INSERT INTO player_permanent_upgrades(player_uuid,upgrade_id,upgrade_level) VALUES('unknown','hp',1)")
    except sqlite3.IntegrityError:
        print('PASS 5/6: foreign key enforcement')
    else:
        raise AssertionError('invalid reference incorrectly accepted')
    con.commit()
    con.close()
    con = sqlite3.connect(path)
    assert con.execute('SELECT COUNT(*) FROM arena_block_journal').fetchone()[0] == 1
    print('PASS 6/6: journal survives reconnect')
    con.close()
print('OFFLINE_SQL_CHECKS: 6/6 PASS')
