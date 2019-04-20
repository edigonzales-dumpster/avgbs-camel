# avgbs-camel
av2gb and gb2av

## Datenmodell
`KS3-20060703.ili` wird lokal vorgehalten, da verschiedene Attribute als JSON-Typ abgebildet werden sollen.

## Entwicklung
SQL-Datei für DDL erzeugen:
```
java -jar /Users/stefan/apps/ili2pg-4.0.0/ili2pg-4.0.0.jar --createBasketCol --createDatasetCol --createFk --createFkIdx --coalesceJson --createEnumTabs --nameByTopic --createImportTabs --smart2Inheritance --dbschema agi_avgbs --createscript sql/agi_avgbs_create.sql --modeldir model --models GB2AV
```

Testimport:
```
java -jar /Users/stefan/apps/ili2pg-4.0.0/ili2pg-4.0.0.jar --dbhost 192.168.50.8 --dbdatabase edit --dbusr ddluser --dbpwd ddluser --createBasketCol --createDatasetCol --createFk --createFkIdx --coalesceJson --createEnumTabs --nameByTopic --smart2Inheritance --dbschema agi_avgbs --modeldir model --models GB2AV --dataset SO0200002401_1622_20190416.xml --import src/test/data/SO0200002401_1622_20190416.xml

java -jar /Users/stefan/apps/ili2pg-4.0.0/ili2pg-4.0.0.jar --dbhost 192.168.50.8 --dbdatabase edit --dbusr ddluser --dbpwd ddluser --createBasketCol --createDatasetCol --createFk --createFkIdx --coalesceJson --createEnumTabs --nameByTopic --smart2Inheritance --dbschema agi_avgbs --modeldir model --models GB2AV --dataset VOLLZUG_SO0200002401_1531_20180105113131.xml --import src/test/data/VOLLZUG_SO0200002401_1531_20180105113131.xml
```

## Betrieb


## Tipps und Tricks

Mit `xmllint` das XML formatieren und die Umlaute nicht verlieren:
```
xmllint --format --noent --noenc --encode UTF-8 SO0200002401_1622_20190416.xml -o SO0200002401_1622_20190416.xml
```

Auf die Schnelle Modelle ändern und Testen wie sie in der DB abgebildet werden:
```
rm -rf ~/.ilicache/

java -jar ~/apps/ili2pg-4.0.0/ili2pg-4.0.0.jar --dbhost 192.168.50.8 --dbdatabase edit --dbusr ddluser --dbpwd ddluser --createBasketCol --createDatasetCol --createFk --createFkIdx --coalesceJson --createEnumTabs --nameByTopic --smart2Inheritance --dbschema agi_avgbs --modeldir model --models GB2AV --dataset SO0200002401_1622_20190416.xml --doSchemaImport --preScript prescript.sql --import src/test/data/SO0200002401_1622_20190416.xml
```
mit `prescript.sql`:
```
DROP SCHEMA agi_avgbs CASCADE;
```

