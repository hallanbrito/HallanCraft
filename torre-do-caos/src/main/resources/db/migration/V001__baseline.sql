-- Schema baseline. All IDs use Minecraft UUID textual representation.
CREATE TABLE player_profiles (
  player_uuid TEXT PRIMARY KEY NOT NULL,
  player_name TEXT NOT NULL,
  permanent_coins INTEGER NOT NULL DEFAULT 0 CHECK(permanent_coins >= 0),
  highest_floor INTEGER NOT NULL DEFAULT 0 CHECK(highest_floor >= 0),
  highest_wave INTEGER NOT NULL DEFAULT 0 CHECK(highest_wave >= 0),
  total_runs INTEGER NOT NULL DEFAULT 0 CHECK(total_runs >= 0),
  updated_at INTEGER NOT NULL
);
CREATE TABLE player_permanent_upgrades (
  player_uuid TEXT NOT NULL,
  upgrade_id TEXT NOT NULL,
  upgrade_level INTEGER NOT NULL DEFAULT 0 CHECK(upgrade_level >= 0),
  PRIMARY KEY(player_uuid, upgrade_id),
  FOREIGN KEY(player_uuid) REFERENCES player_profiles(player_uuid) ON DELETE CASCADE
);
CREATE TABLE active_run_state (
  session_id TEXT PRIMARY KEY NOT NULL,
  slot INTEGER NOT NULL DEFAULT 1 UNIQUE CHECK(slot = 1),
  state TEXT NOT NULL CHECK(state IN (
    'FLOOR_PREPARING', 'WAVE_COMBAT', 'WAVE_INTERMISSION',
    'FLOOR_TRANSITION', 'RUN_DEFEAT_CLEANUP', 'RECOVERING'
  )),
  current_world_id TEXT NOT NULL,
  current_arena_id TEXT NOT NULL,
  current_floor INTEGER NOT NULL CHECK(current_floor >= 1),
  current_wave INTEGER NOT NULL CHECK(current_wave >= 1),
  started_at INTEGER NOT NULL
);
CREATE TABLE active_run_players (
  player_uuid TEXT PRIMARY KEY NOT NULL,
  session_id TEXT NOT NULL,
  status TEXT NOT NULL CHECK(status IN ('ALIVE', 'DOWNED', 'DISCONNECTED')),
  disconnected_at INTEGER,
  FOREIGN KEY(session_id) REFERENCES active_run_state(session_id) ON DELETE CASCADE
);
CREATE TABLE arena_block_journal (
  session_id TEXT NOT NULL,
  world_id TEXT NOT NULL,
  arena_id TEXT NOT NULL,
  x INTEGER NOT NULL,
  y INTEGER NOT NULL,
  z INTEGER NOT NULL,
  original_block_data TEXT NOT NULL,
  restored_in_memory INTEGER NOT NULL DEFAULT 0 CHECK(restored_in_memory IN (0,1)),
  PRIMARY KEY(session_id, world_id, arena_id, x, y, z)
);
CREATE TABLE pending_player_cleanups (
  player_uuid TEXT PRIMARY KEY NOT NULL,
  reason TEXT NOT NULL,
  created_at INTEGER NOT NULL
);
CREATE INDEX ix_arena_journal_recovery
ON arena_block_journal(world_id, arena_id, restored_in_memory);
