# PetsApp
Esercizi con app EarthQuakeApp
https://classroom.udacity.com/courses/ud845

Rif GIT:
https://github.com/udacity/ud845-Pets
https://github.com/udacity/Sunshine-Version-2

Casi risolti:
1) bottone FAB (FloatingActionButton)
2) menu' options
3) salvataggio dati con SQLite: N.B. se il nome del db in chi estende SQLiteOpenHelper e' null, e' un db in memory (scompare quando si fa la close della connessione)
    - https://developer.android.com/training/data-storage/sqlite.html
    - https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html
    - https://developer.android.com/reference/android/content/ContentValues.html
    - https://developer.android.com/reference/android/database/Cursor.html
    - https://developer.android.com/guide/topics/providers/content-provider-basics.html#Query
4) controllo compile-time dei valori corretti in un insieme: https://developer.android.com/reference/android/support/annotation/IntDef
5) ContentResolver, ContentProvider e CursorLoader:
    - https://developer.android.com/guide/topics/manifest/provider-element.html
    - https://developer.android.com/reference/android/content/ContentProvider.html
    - https://developer.android.com/guide/topics/providers/content-provider-creating.html
    - https://developer.android.com/reference/android/content/UriMatcher.html
5.1) schema: Content Provider puo' esporre le sue funzionalita' anche ad altre app esterne + check centralizzato dei dati inseriti
    - comando (Uri): UI -> ContentResolver -> ContentProvider -> DbHelper -> DB
    - risultato (Cursor,Uri,int): DB -> DbHelper -> ContentProvider -> ContentResolver -> UI
      

TODO:
1) sostituire i vari layout con le ConstraintLayout (vedere https://github.com/chrisbanes/cheesesquare)
2) sostituire l'accesso al db usando Room (https://developer.android.com/training/data-storage/room)
3) usare le https://developer.android.com/guide/topics/ui/layout/recyclerview.html

Altri link utili:
Datatypes In SQLite: https://www.sqlite.org/datatype3.html
Data and file storage: https://developer.android.com/guide/topics/data/data-storage.html

Comandi sqlite3 utili (https://www.sqlite.org/cli.html):
- .mode : provides several output options aside from column (".mode tabs" to have tab-separated values, ".mode ascii")
- .help : help generico sugli altri comandi
- .schema <tabella> : print create table statement
- .header on : mostra gli header nei risultati delle select
- .mode column : allinea le righe della select per colonna


Progetti come esercizio:

