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

In einem Exchange werden zu den Mengenangaben und Formeln auch die Einheit und
die physikalische Größe (genauer die Referenz zum Umrechnungsfaktor zu der
physikalischen Referenzgröße für den Fluss: `FlowPropertyFactor`) gespeichert.

Die `ConversionTable` liefert für eine Datenbank effizient die 
Umrechnungsfaktoren in die jeweiligen Referenzgrößen. In der Berechnung und in 
den Ergebnissanzeigen werden Mengen von Flüssen immer in der Referenzgröße und 
deren Einheit angegeben. Wichtig ist allerdings, das der Konvertierungsfaktor 
erst nach der Auswertung von Formeln auf eine Mengenangaben angewendet wird.

## Index der Technologie-Matrix
In der Technologie-Matrix werden die Produkt- und Abfallflüsse eines 
Produktsystems auf Zeilen und Spalten abgebildet. Bisher wurde dafür das
Paar aus Prozess- und Fluss-ID verwendet, um eine Zeile/Spalte in dieser
Matrix eindeutig zu identifiziern:

```java
    LongPair processProduct = LongPair.of(processId, productId);
```

Genauso wurden auch Inputprodukte identifiziert und zum Aufbau der Matrizen für
die Berechnung verwendet. In Produktsystemen wurde dann ein Prozesslink mit den
folgenden Attributen gespeichert:

```java
    puplic class ProcessLink {
        public long providerId;
        public long recipientId;
        public long flowId;
    }
```

Will man aber in einem Prozess  Inputprodukte (bzw. Abfalloutputs) von 
unterschiedlichen Providern im System haben, reicht diese Information nicht mehr
aus, da man nicht weiß, welcher Input (bzw. Output) mit Menge, Einheit, etc. 
zu diesem Link gehört.

### Verwenden von Exchange-IDs
Eine Alternative dieses Problem zu lösen ist die Verwendung von Exchange-IDs
zur Referenzierung von Produkten und Abfällen. Ein Prozesslink könnte in 
openLCA 2.0 im einfachsten Fall so aussehen:

```java
    puplic class ProcessLink {
        public long inputID;
        public long outputID;
    }
```

(Gegebenenfalls können noch Prozess- und Fluss-IDs hinzugefügt werden, um Daten
für die Visualisierungen/Editoren effizienter laden zu können.)

#### Verlinken eines Produktsystems



