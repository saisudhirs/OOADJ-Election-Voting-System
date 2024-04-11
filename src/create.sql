CREATE TABLE candidates (
                            id INTEGER PRIMARY KEY,
                            name TEXT,
                            party TEXT
);

CREATE TABLE voters (
                        id INTEGER PRIMARY KEY,
                        name TEXT
);

CREATE TABLE votes (
                       id INTEGER PRIMARY KEY,
                       voter_id INTEGER REFERENCES voters(id),
                       candidate_id INTEGER REFERENCES candidates(id)
);

CREATE TABLE reset_history (
                               id INTEGER PRIMARY KEY,
                               time_date TIMESTAMP
);

-- Sample insert statements for the candidates table
INSERT INTO candidates (id, name, party) VALUES (1, 'Candidate 1', 'Party A');
INSERT INTO candidates (id, name, party) VALUES (2, 'Candidate 2', 'Party B');
INSERT INTO candidates (id, name, party) VALUES (3, 'Candidate 3', 'Party C');

-- Sample insert statements for the voters table
INSERT INTO voters (id, name) VALUES (1, 'Voter 1');
INSERT INTO voters (id, name) VALUES (2, 'Voter 2');
INSERT INTO voters (id, name) VALUES (3, 'Voter 3');

-- Sample insert statements for the votes table
-- Assuming voters with IDs 1, 2, and 3 have voted for candidates with IDs 1, 2, and 3 respectively
INSERT INTO votes (id, voter_id, candidate_id) VALUES (1, 1, 1);
INSERT INTO votes (id, voter_id, candidate_id) VALUES (2, 2, 2);
INSERT INTO votes (id, voter_id, candidate_id) VALUES (3, 3, 3);

-- Sample insert statements for the reset_history table
-- Assuming reset events are recorded with timestamps
INSERT INTO reset_history (id, time_date) VALUES (1, '2024-04-10 10:00:00');
INSERT INTO reset_history (id, time_date) VALUES (2, '2024-04-09 15:30:00');
