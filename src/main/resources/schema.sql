CREATE TABLE IF NOT EXISTS emprunt (
    id                  SERIAL PRIMARY KEY,
    numero_adherent     VARCHAR(20) NOT NULL,
    code_document       VARCHAR(20) NOT NULL,
    type_document       VARCHAR(10) NOT NULL,
    date_emprunt        DATE        NOT NULL,
    date_retour_prevue  DATE        NOT NULL,
    date_retour         DATE
)
