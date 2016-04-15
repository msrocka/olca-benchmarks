# exlink - openLCA product system linking via exchange IDs

Ziel für openLCA 2.0 ist es, zwei elementare Schwächen im Verlinkungs- und 
Berechnungsmodell von openLCA zu beseitigen:

1. Prozessverbindungen in Produktsystemen werden derzeit in openLCA über die 
   Attribute Prozess-ID des Lieferanten, Prozess-ID des Empfängers und Fluss-ID 
   definiert. Damit ist es aber nicht möglich Verbindungen zu definieren, bei 
   denen z.B. in einem Prozess Inputs mit dem gleichen Fluss von verschiedenen 
   Lieferanten vorkommen. Dies ist insbesondere bei ecoinvent 3 der Fall. 
   Derzeit wird das so umgangen, dass künstliche Flüsse generiert werden. In 
   openLCA 2.0 soll dies behoben werden indem Prozessverbindungen direkt über 
   die IDs der jeweiligen Inputs und Outputs in den Prozessen spezifiziert 
   werden. Hierfür sind zahlreiche Anpassungen notwendig, die unter anderem die 
   folgenden Komponenten betreffen: Berechnungs- und Verlinkungskern, Modell, 
   Datenaustausch (Produktsysteme im JSON-LD-Format), Produktsystemeditor, …
   
2. Abfälle haben derzeit in openLCA keine definierte Rolle. Zum Teil werden sie 
   wie Produktflüsse und zum Teil wie Elementarflüsse behandelt. Das genaue 
   Verhalten ist oft nicht definiert. In openLCA 2.0 sollen Abfälle konsequent 
   als Flüsse der Technosphäre behandelt werden, die entgegengesetzt wie 
   Produktflüsse verlinkt werden: Abfallbehandlungsprozesse nehmen Abfälle als 
   Inputs auf und können bei Abfallproduzenten auf der Outputseite verlinkt 
   werden. Ähnlich wie die Änderung oben, durchzieht dies zahlreiche Komponenten 
   (Verlinkung, Allokation!, grafischer Editor).
   
Beim Umbau des internen Modells sollen gleichzeitig folgende Punkte 
mitberücksichtigt werden:

* Die quantitative Referenz in einem Prozess soll frei wählbar sein und keine so 
  herausragende Rolle mehr haben. Erst beim Anlegen eines Produktsystems muss 
  sie auf Konsistenz geprüft werden (Produktoutput oder Abfallinput)
* Speicherbedarf weiter reduzieren und Berechnungsgeschwindigkeit erhöhen…   

Alle bisherigen Funktionen sollen vollständig erhalten bleiben.

## Berechnungsmodell
### Einheiten und Größen
Input- und Outputmengen können in verschiedenen Einheiten und physikalischen 
Größen angegeben werden. Die möglichen Einheiten und Größen sind abhängig vom 
jeweiligen Fluss. Jeder Fluss hat eine Menge von physikalischen Größen, in denen 
er angegeben werden kann. Es gibt für jeden Fluss genau eine Referenzgröße. Jede 
physikalische Größe hat genau eine Einheitengruppe die wiederum genau eine 
Referenzeinheit hat. 

## Index der Technologie-Matrix