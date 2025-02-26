-- Drop and recreate the cosmetic_ingredient table with proper auto-increment
DROP TABLE IF EXISTS cosmetic_ingredient;

CREATE TABLE cosmetic_ingredient (
    cosmetic_ingredient_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cosmetic_id BIGINT,
    ingredient_id BIGINT,
    FOREIGN KEY (cosmetic_id) REFERENCES cosmetic(cosmetic_id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredient(ingredient_id)
);
