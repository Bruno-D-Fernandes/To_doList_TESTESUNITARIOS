DROP DATABASE IF EXISTS todolist_db;
CREATE DATABASE todolist_db;
USE todolist_db;

CREATE TABLE usuario (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nome          VARCHAR(150) NOT NULL,
  email         VARCHAR(190) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB;

CREATE TABLE task_groups (
  id       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name     VARCHAR(150) NOT NULL,
  owner_id BIGINT UNSIGNED NOT NULL,
  CONSTRAINT fk_group_owner FOREIGN KEY (owner_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- GROUP_MEMBERS (N:N entre usuario e task_groups)
-- ---------------------------------------------------------------------
CREATE TABLE group_members (
  id       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,  
  group_id BIGINT UNSIGNED NOT NULL,
  user_id  BIGINT UNSIGNED NOT NULL,
  role     ENUM('OWNER','ADMIN','MEMBER') NOT NULL DEFAULT 'member',
  CONSTRAINT fk_gm_group FOREIGN KEY (group_id) REFERENCES task_groups(id) ON DELETE CASCADE,
  CONSTRAINT fk_gm_user  FOREIGN KEY (user_id)  REFERENCES usuario(id)     ON DELETE CASCADE
) ENGINE=InnoDB;


-- ---------------------------------------------------------------------
-- TASKS (cada task pertence a UM grupo)
-- ---------------------------------------------------------------------
CREATE TABLE tasks (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    group_id BIGINT UNSIGNED NOT NULL,
    status_id BIGINT UNSIGNED NULL,
    title VARCHAR(200) NOT NULL,
    created_by BIGINT UNSIGNED NOT NULL,
    status ENUM('paused', 'finish', 'working') DEFAULT 'working' NOT NULL,
    due_date DATETIME NULL,
    CONSTRAINT fk_task_group FOREIGN KEY (group_id)
        REFERENCES task_groups (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_task_creator FOREIGN KEY (created_by)
        REFERENCES usuario (id)
        ON DELETE CASCADE
)  ENGINE=INNODB;

-- ---------------------------------------------------------------------
-- TAGS (N:N entre tags e tasks)
-- ---------------------------------------------------------------------
CREATE TABLE tags (
  id       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  group_id BIGINT UNSIGNED NOT NULL,
  name     VARCHAR(50) NOT NULL,
  CONSTRAINT fk_tag_group FOREIGN KEY (group_id) REFERENCES task_groups(id) ON DELETE CASCADE,
  UNIQUE KEY uq_group_tag (group_id, name)
) ENGINE=InnoDB;

CREATE TABLE task_tags (
  task_id BIGINT UNSIGNED NOT NULL,
  tag_id  BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (task_id, tag_id),
  CONSTRAINT fk_tt_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
  CONSTRAINT fk_tt_tag  FOREIGN KEY (tag_id)  REFERENCES tags(id)  ON DELETE CASCADE
) ENGINE=InnoDB;