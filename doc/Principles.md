# Prinzipien

## KEINE JOINS auf die Exchange-Tabelle
Der Benchmark `ExchangeTableJoin` zeigt, dass es signifikant schneller
ist Ergebnisse in eine Map zwischenzuspeichern und beim Durchlaufen
der Tabelle abzufragen, als sie über einen JOIN in die Abfrage einzubeziehen
(0.3 im Vergleich zu 7.3 Sekunden):

    Benchmark                                Mode  Cnt  Score   Error  Units
    ExchangeTableJoin.scanWithFlowTypeTable  avgt    5  0.261 ± 0.015   s/op
    ExchangeTableJoin.scanWithJoin           avgt    5  7.292 ± 0.436   s/op 