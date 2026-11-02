-- =============================================================================
-- LOGIA LOG v3.1 (TARGET-AGNOSTIC EDITION)
-- PostgreSQL 18.4 Core Database Schema (Flyway V1 Baseline)
-- =============================================================================
-- Catatan: PostgreSQL 18 menyediakan fungsi native uuidv7() untuk time-ordered UUID!

-- 1. Dynamic Target Profiles (Slot Agnostik / Opsional via UI)
CREATE TABLE IF NOT EXISTS target_profiles (
    id                      UUID PRIMARY KEY DEFAULT uuidv7(),
    service_name            VARCHAR(150) NOT NULL UNIQUE,
    base_api_url            VARCHAR(500),
    github_repo_url         VARCHAR(500),
    github_branch           VARCHAR(120) DEFAULT 'main',
    github_token_encrypted  TEXT,
    readonly_db_jdbc_url    VARCHAR(500),
    readonly_db_username    VARCHAR(150),
    readonly_db_pass_enc    TEXT,
    is_active               BOOLEAN NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 2. Error Aggregates (Deduplication & Recurring Error Tracker)
CREATE TABLE IF NOT EXISTS error_aggregates (
    fingerprint_hash    CHAR(64) PRIMARY KEY,
    service_name        VARCHAR(150) NOT NULL,
    exception_class     VARCHAR(300),
    target_file         VARCHAR(300),
    target_line         INT,
    occurrence_count    BIGINT NOT NULL DEFAULT 1,
    first_seen          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_seen           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    status              VARCHAR(40) NOT NULL DEFAULT 'OPEN'
);

CREATE INDEX IF NOT EXISTS idx_error_aggregates_service_status
    ON error_aggregates (service_name, status, last_seen DESC);

-- 3. App Logs (Range-Partitioned by created_at + GIN Index on JSONB metadata)
CREATE TABLE IF NOT EXISTS app_logs (
    id                  UUID NOT NULL DEFAULT uuidv7(),
    service_name        VARCHAR(150) NOT NULL,
    log_level           VARCHAR(20) NOT NULL,
    trace_id            VARCHAR(128),
    fingerprint_hash    CHAR(64),
    raw_message         TEXT NOT NULL,
    stack_trace         TEXT,
    metadata            JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

CREATE TABLE IF NOT EXISTS app_logs_default PARTITION OF app_logs DEFAULT;

CREATE INDEX IF NOT EXISTS idx_app_logs_created_at ON app_logs (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_app_logs_service_level ON app_logs (service_name, log_level, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_app_logs_trace_id ON app_logs (trace_id) WHERE trace_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_app_logs_fingerprint ON app_logs (fingerprint_hash) WHERE fingerprint_hash IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_app_logs_metadata_gin ON app_logs USING GIN (metadata jsonb_path_ops);

-- 4. API Test Collections & Requests (Postman-Style API Testing Studio)
CREATE TABLE IF NOT EXISTS api_test_collections (
    id              UUID PRIMARY KEY DEFAULT uuidv7(),
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    env_variables   JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS api_test_requests (
    id              UUID PRIMARY KEY DEFAULT uuidv7(),
    collection_id   UUID REFERENCES api_test_collections(id) ON DELETE CASCADE,
    name            VARCHAR(200) NOT NULL,
    http_method     VARCHAR(16) NOT NULL,
    url             TEXT NOT NULL,
    query_params    JSONB NOT NULL DEFAULT '[]'::jsonb,
    auth_type       VARCHAR(40) NOT NULL DEFAULT 'NO_AUTH',
    auth_config_enc TEXT,
    headers         JSONB NOT NULL DEFAULT '[]'::jsonb,
    body_type       VARCHAR(40) NOT NULL DEFAULT 'NONE',
    body_payload    TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 5. API Test Histories (Auto-Correlated via X-Sentinel-Trace-Id)
CREATE TABLE IF NOT EXISTS api_test_histories (
    id                  UUID PRIMARY KEY DEFAULT uuidv7(),
    request_id          UUID REFERENCES api_test_requests(id) ON DELETE SET NULL,
    trace_id            VARCHAR(128) NOT NULL,
    http_method         VARCHAR(16) NOT NULL,
    resolved_url        TEXT NOT NULL,
    status_code         INT NOT NULL,
    duration_ms         INT NOT NULL,
    response_size_bytes INT NOT NULL DEFAULT 0,
    response_headers    JSONB NOT NULL DEFAULT '{}'::jsonb,
    response_body       TEXT,
    executed_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_api_test_histories_trace_id ON api_test_histories (trace_id);

-- 6. Deep GitHub 7-Layer AI Diagnostic Audits
CREATE TABLE IF NOT EXISTS github_code_audits (
    id                      UUID PRIMARY KEY DEFAULT uuidv7(),
    fingerprint_hash        CHAR(64) NOT NULL REFERENCES error_aggregates(fingerprint_hash) ON DELETE CASCADE,
    repo_url                VARCHAR(500) NOT NULL,
    branch_name             VARCHAR(120) NOT NULL,
    commit_sha              VARCHAR(64),
    affected_files          JSONB NOT NULL DEFAULT '[]'::jsonb,
    db_ddl_snapshot         TEXT,
    analysis_7_layers       JSONB NOT NULL,
    suggested_patch_diff    TEXT,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_github_code_audits_fingerprint
    ON github_code_audits (fingerprint_hash, created_at DESC);

