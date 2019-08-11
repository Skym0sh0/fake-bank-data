INSERT INTO CATEGORY(ID, NAME, DESCRIPTION, PATTERN, SORTING_KEY)
VALUES
('3baf44d4-b166-49bd-bf67-999999999999', 'Rest', 'Restliches Zeugs, was keiner Kategorie zugeordnet werden kann', '.*', -99999),
('3baf44d4-b166-49bd-bf67-ab28876ab800', 'Amazon', 'Amazon Bestellungen', '.*Amazon.*', 0),
('3baf44d4-b166-49bd-bf67-ab28876ab801', 'Tanken', 'Tanken', 'Tanken', 0),
('3baf44d4-b166-49bd-bf67-ab28876ab802', 'Weihnachten', 'Weihnachtsgeschenke', '.*Weihnacht.*', 1),
('3baf44d4-b166-49bd-bf67-ab28876ab803', 'Gehalt', 'Gehaltzahlungen', '.*Scoop.*|Infolog|Netcologne|FH-Job.*|FH-Praktikum|.*C\+\+', 0),
('3baf44d4-b166-49bd-bf67-ab28876ab804', 'BU-Versicherung', 'Berufsunfähigkeitsversicherung', 'BU-Versicherung.*', 0),
('3baf44d4-b166-49bd-bf67-ab28876ab805', 'Krankenversicherung', 'Krankenversicherung', 'AOK', 0),
('3baf44d4-b166-49bd-bf67-ab28876ab806', 'Bargeld', 'Bargeld vom Bankautomaten', 'Bankautomat', 0),
('3baf44d4-b166-49bd-bf67-ab28876ab807', 'Lebenskosten', 'Lebenskosten und Miete etc.', 'Miete|Mietpuffer', 0),
('3baf44d4-b166-49bd-bf67-ab28876ab808', 'Sport', 'Sportliche Aktivitäten und Hobbies', 'Kletter.*|Sieglar TV.*', 1),
('3baf44d4-b166-49bd-bf67-ab28876ab809', 'Computerspiele', 'Games und Steam Einkäufe', 'Steam.*.*', 0),
('3baf44d4-b166-49bd-bf67-ab28876ab810', 'Coding', 'Coding und Entwicklungszeugs', 'Daler Solutions|Capgemini.*', 0),
('3baf44d4-b166-49bd-bf67-ab28876ab811', 'Auto', 'Alles rund ums Auto', 'Auto Steuer.*|Auto Reifen.*|Autoradio|BMW|Auto Versicherung|.*Auto Reparatur|.*Autoreparatur|ADAC|Achsvermessung.*|Reifen', 0),
('3baf44d4-b166-49bd-bf67-ab28876ab812', 'Steuern', 'Alles rund um Steuern', 'Steuererklärung.*', 0)
;
