# Prinzipien

## Keine Joins auf die Exchange-Tabelle
Der Benchmark `ExchangeTableJoin` zeigt, dass es signifikant schneller
ist Ergebnisse in eine Map zwischenzuspeichern und beim Durchlaufen
der Tabelle abzufragen, als sie über einen JOIN in die Abfrage einzubeziehen
(0.3 im Vergleich zu 7.3 Sekunden):

    Benchmark                                Mode  Cnt  Score   Error  Units
    ExchangeTableJoin.scanWithFlowTypeTable  avgt    5  0.261 ± 0.015   s/op
    ExchangeTableJoin.scanWithJoin           avgt    5  7.292 ± 0.436   s/op
    
    
## Zugriff auf Ergebnisse von SQL Abfragen per Index
Bei einem `ResultSet`, das man als Ergebnis von einer SQL Abfrage bekommen
kann, hat man die Möglichkeit dieses als   