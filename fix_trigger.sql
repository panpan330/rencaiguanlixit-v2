DELIMITER //

CREATE TRIGGER trg_talent_profile_update 
AFTER UPDATE ON sys_talent_profile 
FOR EACH ROW 
BEGIN 
    IF (OLD.real_name != NEW.real_name) 
       OR (OLD.primary_domain != NEW.primary_domain) 
       OR (OLD.education_level != NEW.education_level AND OLD.education_level IS NOT NULL) 
       OR (OLD.research_direction != NEW.research_direction AND OLD.research_direction IS NOT NULL) 
       OR (OLD.phone != NEW.phone AND OLD.phone IS NOT NULL)
       OR (OLD.phone IS NULL AND NEW.phone IS NOT NULL)
       OR (OLD.education_level IS NULL AND NEW.education_level IS NOT NULL)
       OR (OLD.research_direction IS NULL AND NEW.research_direction IS NOT NULL)
    THEN 
        INSERT INTO `biz_talent_profile_history` (
            `profile_id`, `old_real_name`, `old_primary_domain`, `old_education_level`, `old_research_direction`, `action_type`
        ) VALUES (
            OLD.id, OLD.real_name, OLD.primary_domain, OLD.education_level, OLD.research_direction, 'UPDATE'
        );
    END IF; 
END //

DELIMITER ;
