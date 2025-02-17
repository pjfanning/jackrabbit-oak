Oak Runnable Jar
================

This jar contains everything you need for a simple Oak installation. 

The following runmodes are currently available:

    * backup          : Backup an existing Oak repository
    * check           : Check the FileStore for inconsistencies
    * checkpoints     : Manage checkpoints
    * clusternodes    : Display DocumentMK cluster node information
    * compact         : Segment compaction on a TarMK repository
    * console         : Start an interactive console
    * datastorecacheupgrade : Upgrades the JR2 DataStore cache
    * datastorecheck  : Consistency checker for data store 
    * datastore       : Maintenance operations for the for data store 
    * debug           : Print status information about an Oak repository
    * explore         : Starts a GUI browser based on java swing
    * export          : Export repository content as json
    * frozennoderefsbyscanning : Scan for nt:frozenNode references via query
    * frozennoderefsusingindex : Scan for nt:frozenNode references via /oak:index
    * garbage         : Identifies blob garbage on a DocumentMK repository
    * help            : Print a list of available runmodes
    * history         : Trace the history of a node
    * iotrace         : Collect a trace of segment store read accesses 
    * recovery        : Run a _lastRev recovery on a DocumentMK repository
    * resetclusterid  : Resets the cluster id
    * restore         : Restore a backup of an Oak repository
    * revisions       : Revision GC on a DocumentMK
    * server          : Run the Oak Server
    * tarmkdiff       : Show changes between revisions on TarMk
    * tika            : Performs text extraction
    * unlockUpgrade   : Unlock a DocumentMK upgrade to a newer version
    * upgrade         : Migrate existing Jackrabbit 2.x repository to Oak
    

Some of the features related to Jackrabbit 2.x are provided by oak-run-jr2 jar. See
the [Oak Runnable JR2](#jr2) section for more details.

Logging
-------

Oak run uses [Logback](https://logback.qos.ch/) for logging. To customize the logging
you can specify a custom logback config file via `logback.configurationFile` system property
 
    java -Dlogback.configurationFile=./logback.xml -jar oak-run-*.jar console /path/to/oak/repository
    
See [here](https://github.com/apache/jackrabbit-oak/blob/trunk/oak-run/src/main/resources/logback.xml) for the default 
logback config file used

Command Line Format
-------------------

Oak run uses [joptsimple](http://jopt-simple.github.io/jopt-simple/) library for parsing the command line 
options. 

* A option's argument can occur:
    * `--foo=bar` - right up against the option separated by an equals sign (=)
    * `--foo bar` - in the position on the command line after the option
* `--foo=bar,baz` - Sets multiple values for option `foo` separated by `,`
* `--foo` - Enables `foo` option where foo is a boolean option. 
* Most commands provide help via `-h` option
* An argument consisting only of two hyphens (--) signals that the remaining arguments are to be treated as non-options.

Refer to [examples](http://jopt-simple.github.io/jopt-simple/examples.html) for more details

----

See the subsections below for more details on how to use these modes.

Backup
------

See the [official documentation](http://jackrabbit.apache.org/oak/docs/nodestore/segment/overview.html#backup).

Restore
-------

See the [official documentation](http://jackrabbit.apache.org/oak/docs/nodestore/segment/overview.html#restore).

Debug
-----

See the [official documentation](http://jackrabbit.apache.org/oak/docs/nodestore/segment/overview.html#debug).

IOTrace
-----

See the [official documentation](http://jackrabbit.apache.org/oak/docs/nodestore/segment/overview.html#iotrace).


Console
-------

The 'console' mode allows to work with an interactive console and browse an
existing oak repository. Type ':help' within the console to get a list of all
supported commands. The console currently supports TarMK and MongoMK. To start
the console for a TarMK repository, use:

    $ java -jar oak-run-*.jar console /path/to/oak/repository
    
To start the console for a DocumentMK/Mongo repository, use:

    $ java -jar oak-run-*.jar console mongodb://host

To start the console for a DocumentMK/RDB repository, see the [documention for oak-run on RDB](https://jackrabbit.apache.org/oak/docs/nodestore/document/rdb-document-store.html#oak-run).
    
To start the console connecting to a repository in read-write mode, use either of:

    $ java -jar oak-run-*.jar console --read-write /path/to/oak/repository
    $ java -jar oak-run-*.jar console --read-write mongodb://host
    $ java -jar oak-run-*.jar console --read-write --rdbjdbcuser username --rdbjdbcpasswd password console jdbc:...

To specify FDS path while connecting to a repository, use `--fds-path` option (valid for segment and document repos):

    $ java -jar oak-run-*.jar console --fds-path /path/to-data/store /path/to/oak/repository

Console is based on [Groovy Shell](http://groovy.codehaus.org/Groovy+Shell) and hence one 
can use all Groovy constructs. It also exposes the `org.apache.jackrabbit.oak.console.ConsoleSession`
instance as through `session` variable. For example when using SegmentNodeStore you can 
dump the current segment info to a file

    > new File("segment.txt") << session.workingNode.segment.toString()
    
In above case the `workingNode` captures the current `NodeState` which in case of 
Segment/TarMK is `SegmentNodeState`

You can also load external script at launch time via passing an extra argument as shown 
below

    $ java -jar oak-run-*.jar console mongodb://host ":load /path/to/script.groovy"

Explore
-------

The 'explore' mode starts a desktop browser GUI based on java swing which allows for read-only
browsing of an existing oak repository.

    $ java -jar oak-run-*.jar explore /path/to/oak/repository [--skip-size-check]

Microsoft Azure node stores are also supported using the following command.  The secret key must be supplied as an environment variable `AZURE_SECRET_KEY`.  

    $ java -jar oak-run-*.jar explore az:https://myaccount.blob.core.windows.net/container/repository [--skip-size-check]

If using a Shared Access Signature, the SAS token has to be appended to the azure URI.

    $ java -jar oak-run-*.jar explore az:https://myaccount.blob.core.windows.net/container/repository?<sas-token> [--skip-size-check]

frozennoderefsbyscanning
------------------------

This command executes a potentially expensive (!) traversing query searching for
all properties formatted as a UUID (incl String, Reference types) and verifies
if they represent (potential) references to nt:frozenNode.

Since this is a rather expensive command, consider using frozennoderefsusingindex
(at least first) instead.

If this is used eg on a MongoDB, consider running the command against
a secondary MongoDB node, such as to not overload the primary MongoDB node.

This tool is part of the effort to change the default nt:frozenNode node type
definition to no longer be a mix:referenceable (see OAK-9134). Even though
existing definitions aren't modified, the tool can be used to verify if
an existing repository would potentially be in violation of OAK-9134 - ie if
there are existing use cases of nt:frozenNode being a mix:referenceable.


frozennoderefsusingindex
------------------------

This command browses through /oak:index/references and verifies if they
represent references to nt:frozenNode.

If this is used eg on a MongoDB, consider running the command against
a secondary MongoDB node, such as to not overload the primary MongoDB node.

This tool is part of the effort to change the default nt:frozenNode node type
definition to no longer be a mix:referenceable (see OAK-9134). Even though
existing definitions aren't modified, the tool can be used to verify if
an existing repository would potentially be in violation of OAK-9134 - ie if
there are existing use cases of nt:frozenNode being a mix:referenceable.


History
-------

See the [official documentation](http://jackrabbit.apache.org/oak/docs/nodestore/segment/overview.html#history).

Check
-----

See the [official documentation](http://jackrabbit.apache.org/oak/docs/nodestore/segment/overview.html#check).

Compact
-------

See the [official documentation](http://jackrabbit.apache.org/oak/docs/nodestore/segment/overview.html#compact).

Checkpoints
-----------

The 'checkpoints' mode can be used to list or remove repository checkpoints
To start this mode, use:

    $ java -jar oak-run-*.jar checkpoints { /path/to/oak/repository | mongodb://host:port/database } [list|rm-all|rm-unreferenced|rm <checkpoint>]

The 'list' option (treated as a default when nothing is specified) will list all existing checkpoints.
The 'rm-all' option will wipe clean the 'checkpoints' node.
The 'rm-unreferenced' option will remove all checkpoints except the one referenced from the async indexer (/:async@async).
The 'rm <checkpoint>' option will remove a specific checkpoint from the repository.
The 'info <checkpoint>' option will return metadata information for the given checkpoint.
The 'set <checkpoint> <name> \[<value>\]' option will set the metadata property. If the value is omitted, the property will be removed. 

<a name="tika"></a>
Tika
----

The 'tika' mode enables performing text extraction, report generation and 
csv generation required for text extraction


    Apache Jackrabbit Oak 1.4-SNAPSHOT
    Non-option arguments:                                                         
    tika [extract|report|generate]                                                
    report   : Generates a summary report related to binary data                  
    extract  : Performs the text extraction                                       
    generate : Generates the csv data file based on configured NodeStore/BlobStore
    
    Option                 Description                            
    ------                 -----------                            
    -?, -h, --help         show help                              
    --data-file <File>     Data file in csv format containing the 
                             binary metadata                      
    --fds-path <File>      Path of directory used by FileDataStore
    --nodestore            NodeStore detail                       
                             /path/to/oak/repository | mongodb:   
                             //host:port/database                 
    --path                 Path in repository under which the     
                             binaries would be searched           
    --pool-size <Integer>  Size of the thread pool used to        
                             perform text extraction. Defaults to 
                             number of cores on the system        
    --store-path <File>    Path of directory used to store        
                             extracted text content               
    --tika-config <File>   Tika config file path   

<a name="tika-csv"></a>
### CSV File Format

Text extraction tool reads a csv file which contains details regarding those
binary files from which text needs to be extracted. Entries in csv file look like
below

```
43844ed22d640a114134e5a25550244e8836c00c#28705,28705,"application/octet-stream",,"/content/activities/jcr:content/folderThumbnail/jcr:content"
43844ed22d640a114134e5a25550244e8836c00c#28705,28705,"application/octet-stream",,"/content/snowboarding/jcr:content/folderThumbnail/jcr:content"
...
```

Where the columns are in following order

1. BlobId - Value of [Jackrabbit ContentIdentity](http://jackrabbit.apache.org/api/2.0/org/apache/jackrabbit/api/JackrabbitValue.html)
2. Length
3. jcr:mimeType
4. jcr:encoding
5. path of parent node    

The csv file can be generated programmatically. For Oak based repositories
it can be generated via `generate` command. 

### Generate

CSV file required for `extract` and `report` can  be generated via `generate` 
mode

    java -jar oak-run.jar tika \  
    --fds-path /path/to/datastore \
    --nodestore /path/to/segmentstore --data-file dump.csv generate

Above command would scan the NodeStore and create the csv file. This file can 
then be passed to `extract` command
    
### Report

Tool can generate a summary report from a [csv](#tika-csv) file

    java -jar oak-run.jar tika \ 
        --data-file /path/to/binary-stats.csv report

The report provides a summary like

```
14:39:05.402 [main] INFO  o.a.j.o.p.tika.TextExtractorMain - MimeType Stats
        Total size         : 89.3 MB
        Total indexed size : 3.4 MB
        Total count        : 1048

               Type                 Indexed   Supported    Count       Size   
___________________________________________________________________________________
application/epub+zip              |      true|      true|  1       |    3.4 MB
image/png                         |     false|      true|  544     |   40.2 MB
image/jpeg                        |     false|      true|  444     |   34.0 MB
image/tiff                        |     false|      true|  11      |    6.1 MB
application/x-indesign            |     false|     false|  1       |    3.7 MB
application/octet-stream          |     false|     false|  39      |    1.2 MB
application/x-shockwave-flash     |     false|     false|  4       |  372.2 kB
application/pdf                   |     false|     false|  3       |  168.3 kB
video/quicktime                   |     false|     false|  1       |   95.9 kB
```

### Extract

Extraction can be performed via following command

    java -cp oak-run.jar:tika-app-1.8.jar \
    org.apache.jackrabbit.oak.run.Main tika \
    --data-file binary-stats.csv \
    --store-path ./store 
    --fds-path /path/to/datastore  extract
    
You would need to provide the tika-app jar which contains all the parsers. 
It can be downloaded from [here](https://tika.apache.org/download.html). 
Extraction would then be performed in a multi threaded mode. Extracted text
would be stored in the `store-path`

Upgrade
-------

The 'upgrade' mode allows to migrate the contents of an existing
Jackrabbit 2.x repository to Oak. To run the migration, use:

    $ java -jar oak-run-*.jar upgrade [--datastore] \
          /path/to/jackrabbit/repository [/path/to/jackrabbit/repository.xml] \
          { /path/to/oak/repository | mongodb://host:port/database }

The source repository is opened from the given repository directory, and
should not be concurrently accessed by any other client. Repository
configuration is read from the specified configuration file, or from
a `repository.xml` file within the repository directory if an explicit
configuration file is not given.

The target repository is specified either as a local filesystem path to
a directory (which will be automatically created if it doesn't already exist)
of a new TarMK repository or as a MongoDB client URI that specifies the
location of a MongoDB database where a new DocumentMK repository.

The `--datastore` option (if present) prevents the copying of binary data
from a data store of the source repository to the target Oak repository.
Instead the binaries are copied by reference, and you need to make the
source data store available to the new Oak repository.

The content migration will automatically adjust things like node type,
privilege and user account settings that work a bit differently in Oak.
Unsupported features like same-name-siblings are migrated on a best-effort
basis, with no strict guarantees of completeness. Warnings will be logged
for any content inconsistencies that might be encountered; such content
should be manually reviewed after the migration is complete. Note that
things like search index configuration work differently in Oak than in
Jackrabbit 2.x, and will need to be manually recreated after the migration.
See the relevant documentation for more details.

Oak server mode
---------------

The Oak server mode starts a NodeStore or full Oak instance with the
standard JCR plugins and makes it available over a simple HTTP mapping 
defined in the `oak-http` component. To start this mode, use:

    $ java -jar oak-run-*.jar server [uri] [fixture] [options]

If no arguments are specified, the command starts an in-memory repository
and makes it available at http://localhost:8080/. Specify an `uri` and a
`fixture` argument to change the host name and port and specify a different
repository backend.

The optional fixture argument allows to specify the repository implementation
to be used. The following fixtures are currently supported:

| Fixture              | Description                                           |
|----------------------|-------------------------------------------------------|
| Jackrabbit(*)        | Jackrabbit with the default embedded Derby  bundle PM |
| Oak-Memory           | Oak with default in-memory storage                    |
| Oak-MemoryNS         | Oak with default in-memory NodeStore                  |
| Oak-Mongo            | Oak with the default Mongo backend                    |
| Oak-Mongo-DS         | Oak with the default Mongo backend and DataStore      |
| Oak-MongoNS          | Oak with the Mongo NodeStore                          |
| Oak-Segment-Tar      | Oak with the Tar backend (aka Segment NodeStore)      |
| Oak-Segment-Tar-DS   | Oak with the Tar backend and DataStore                |

Jackrabbit fixture requires [Oak Runnable JR2 jar](#jr2)

Depending on the fixture the following options are available:

    --cache 100            - cache size (in MB)
    --host localhost       - MongoDB host
    --port 27101           - MongoDB port
    --db <name>            - MongoDB database (default is a generated name)
    --clusterIds           - Cluster Ids for the Mongo setup: a comma separated list of integers
    --base <file>          - Tar: Path to the base file
    --mmap <64bit?>        - TarMK memory mapping (the default on 64 bit JVMs)
    --rdbjdbcuri           - JDBC URL for RDB persistence
    --rdbjdbcuser          - JDBC username (defaults to "")
    --rdbjdbcpasswd        - JDBC password (defaults to "")
    --rdbjdbctableprefix   - for RDB persistence: prefix for table names (defaults to "")

Examples:

    $ java -jar oak-run-*.jar server
    $ java -jar oak-run-*.jar server http://localhost:4503 Oak-Segment-Tar --base myOak
    $ java -jar oak-run-*.jar server http://localhost:4502 Oak-Mongo --db myOak --clusterIds c1,c2,c3

See the documentation in the `oak-http` component for details about the available functionality.


Cluster Nodes
=============

The clusternodes mode displays information about the status of the cluster nodes
in a DocumentMK repository. It can be invoked like this:

    $ java -jar oak-run-*.jar clusternodes [options] mongodb://host:port/database

(or, for RDBMK instances, use "jdbc:...").

The following clusternodes options (with default values) are currently supported:

    --clusterId         - DocumentMK clusterId (no default)
    --raw               - List raw entries in JSON format
    --verbose           - Be more verbose

Example output for `--verbose`:

~~~
Id    State          Started LeaseEnd Left RecoveryBy      LastRootRev    OakVersion
 1 INACTIVE 20190125T110237Z        -    -          - r16884ad047c-0-1 1.12-SNAPSHOT
~~~

Note that `RecoveryBy` will display the cluster node id of the node which
currently recovers this node, or `!` when recovery is needed.

`LeaseEnd` and `Left` will be displayed for active nodes (where `Left` is
the remaining time for the lease update in seconds; when it gets negative,
the system is in trouble).


Recovery Mode
=============

The recovery mode can be used to check the consistency of `_lastRev` fields
of a DocumentMK repository. It can be invoked like this:

    $ java -jar oak-run-*.jar recovery [options] mongodb://host:port/database [dryRun] --clusterId id
    
(or, for RDBMK instances, use "jdbc:...").

The following recovery options are currently supported:

    --clusterId         - DocumentMK clusterId (no default)

The recovery tool will only perform the check and fix for the given clusterId.
The tool will fix the documents it identified, unless the `dryRun` keyword is
specified.

Garbage
=======

The garbage mode can the used to identify blob garbage still referenced by
documents in a DocumentMK repository. It can be invoked like this:

    $ java -jar oak-run-*.jar garbage [options] mongodb://host:port/database

(or, for RDBMK instances, use "jdbc:...").

The following recovery options (with default values) are currently supported:

    --clusterId         - DocumentMK clusterId (default: 0 -> automatic)

The tool will scan the store for documents with blob references and print a
report with the top 100 documents with blob references considered garbage. The
rank is based on the size of the referenced blobs.

<a name="jr2"></a>
Oak Runnable Jar - JR 2
===============================

This jar provides Jackrabbit 2.x related features

The following runmodes are currently available:

    * upgrade     : Upgrade from Jackrabbit 2.x repository to Oak.
    * benchmark   : Run benchmark tests against Jackrabbit 2.x repository fixture.
    * server      : Run the JR2 Server.

Oak Mongo Shell Helpers
=======================

To simplify making sense of data created by Oak in Mongo a javascript file oak-mongo.js
is provided. It includes [some useful function][1] to navigate the data in Mongo

    $ wget https://s.apache.org/oak-mongo.js
    $ mongo localhost/oak --shell oak-mongo.js
    MongoDB shell version: 2.6.3
    connecting to: localhost/oak
    type "help" for help
    > oak.countChildren('/oak:index/')
    356787
    > oak.getChildStats('/oak:index')
    { "count" : 356788, "size" : 127743372, "simple" : "121.83 MB" }
    > oak.getChildStats('/')
    { "count" : 593191, "size" : 302005011, "simple" : "288.01 MB" }
    >
    
For reporting any issue related to Oak the script provides a function to collect important stats and 
can be dumped to a file

    $ mongo localhost/oak --eval "load('/path/to/oak-mongo.js');printjson(oak.systemStats());" --quiet > oak-stats.json

[1]: http://jackrabbit.apache.org/oak/docs/oak-mongo-js/oak.html

Oak TarMK Revision Diff
=======================

See the [official documentation](http://jackrabbit.apache.org/oak/docs/nodestore/segment/overview.html#diff).

Oak DataStore Check
-------------------

Consistency checker for the DataStore.
Also can be used to list all the blob references in the node store and all the blob ids available in the data store. 
Use the following command:

    $ java -jar oak-run-*.jar datastorecheck [--id] [--ref] [--consistency] \
            [--store <path>|<mongo_uri>] \
            [--s3ds <s3ds_config>|--fds <fds_config>|--nods] \
            [--dump <path>] \
            [--repoHome <local_repository_root>]
            [--track]
            [--verbose]
            [--verboseRootPath]

The following options are available:

    --id             - List all the ids in the data store
    --ref            - List all the blob references in the node store
    --consistency    - List all the missing blobs by doing a consistency check
    Atleast one of the above should be specified
    
    --store          - Path to the segment store of mongo uri (Required for --ref & --consistency option above)
    --dump           - Path where to dump the files (Optional). Otherwise, files will be dumped in the user tmp directory.
    --s3ds           - Path to the S3DataStore configuration file
    --azureblobds    - Path to the AzureDataStore configuration file
    --fds            - Path to the FileDataStore configuration file ('path' property is mandatory)
    --nods           - To check for misconfigured external references when no data store should be there (Use instead of --s3ds or --fds)
    --repoHome       - Path of the local reposity home folder. Mandatory for --consistency & --track options 
    --track          - Whether to force override the tracked ids (Valid only for --id & --consistency options)
    --verbose        - Outputs backend friendly blobids and also adds the node path (for SegmentNodeStore) from where referred. 
                       This options would typically be a slower option since, it requires the whole repo traversal.  
                       Adds the sub-directories created in FDS and the changes done for S3/Azure when stored in the respective container.
    --verboseRootPath- Paths under which backend friendly blobids are required (Optional). If not specified, then --verbose uses "/" as the default path. For example,
                       to list all blobids under /oak:index and /content/oak:index, use --verboseRootPath /oak:index,/content/oak:index (If providing more than one arguments to this option, 
                       use comma as a delimiter).
Note:

The command to be executed for S3DataStore

    java -classpath oak-run-*.jar \
        org.apache.jackrabbit.oak.run.Main \
        datastorecheck --id --ref --consistency \
        --store <path>|<mongo_uri> \
        --s3ds <s3ds_config> \
        --dump <dump_path>
        --repoHome <repo_home>

The config files should be formatted according to the OSGi configuration admin specification

    E.g.
    cat > org.apache.jackrabbit.oak.plugins.S3DataStore.config << EOF 
    accessKey="XXXXXXXXX"
    secretKey="YYYYYY"
    s3Bucket="bucket1"
    s3Region="region1"
    EOF
    
    cat > org.apache.jackrabbit.oak.plugins.FileDataStore.config << EOF 
    path="/data/datastore"
    EOF        
    
    
Oak DataStore
-------------------

Maintenance commands for the DataStore:
* Data store garbage collection
* Data store consistency check


    $ java -jar oak-run-*.jar datastore [--check-consistency|--collect-garbage [true]] \
            [--s3ds <s3ds_config>|--fds <fds_config>|--azureds <s3ds_config>|fake-ds-path <ds_path>] \
            [--out-dir <output_path>] \
            [--work-dir <temporary_path>] \
            [--max-age <seconds>] \
            [--verbose] \
            [--verboseRootPath] \
            [--useDirListing] \
            [<store_path>|<mongo_uri>]
            [--metrics] [--export-metrics]

The following operations are available:
    
    --collect-garbage          - Execute garbage collection on the data store. If only mark phase to be run specify a true parameter.
    --check-consistency        - List all the missing blobs by doing a consistency check.
    --dump-ref                 - List all the blob references in the node store
    --dump-id                  - List all the ids in the data store
    --get-metadata             - Retrieves a machine readable format GC datastore metadata
                                 e.g. <repoId>|<earliestRef_start_timestamp_secs>|<earliestRef_mark_timestamp_secs>|[*-] * for local repo id

The following options are available:

    --work-dir              - Path to use for temporary files and directories (Optional). Otherwise, files will be dumped in the user temp directory.
    --out-dir               - Path where to dump the files (Optional). Otherwise, files will be dumped in the current directory.
    --ds-read-write         - Required option to open the datastore in read-write mode.
    --s3ds                  - Path to the S3DataStore configuration file.
    --azureds               - Path to the AzureDataStore configuration file.
    --fds                   - Path to the FileDataStore configuration file ('path' property is mandatory).
    --fake-ds-path          - To check for misconfigured external references when no data store should be there.
    --max-age               - Corresponds to the OSGi 'maxBlobGcAgeInSecs' property and specifies the time interval from now with only older blobs being deleted.
    --verbose               - Outputs backend friendly blobids and also adds the node path (for SegmentNodeStore) from where referred. 
                               This options would typically be a slower option since, it requires the whole repo traversal.  
                               Adds the sub-directories created in FDS to the id path and the changes done to the id for S3/Azure when stored in the respective container.
    --verboseRootPath       - Paths under which backend friendly blobids are required (Optional). If not specified, then --verbose uses "/" as the default path. For example,
                              to list all blobids under /oak:index and /content/oak:index, use --verboseRootPath /oak:index,/content/oak:index (If providing more than one arguments to this option, 
                                 use comma as a delimiter).
                                 This option is NOT available for the collect-garbage operation. If specified with collect-garbage, the command execution will throw
                                 an exception.
    --verbosePathInclusionRegex- A Regex that can be used to limit the scan during traversal to a specific inclusion list of nodes identified by the regex.
                                 For example , to look for blob refrences under specific paths such as /b1/b2/foo, /c1/c2/foo under the rootPath /a
                                 use --verboseRootPath /a --verbosePathInclusionRegex /*/*/foo
                                 This option is only available when --verboseRootPath is used.
    --useDirListing         - Use dirListing property for efficient reading of Lucene index files.
    <store_path|mongo_uri>     - Path to the tar segment store or the segment azure uri as specified in 
                                 http://jackrabbit.apache.org/oak/docs/nodestore/segment/overview.html#remote-segment-stores
                                 or if Mongo NodeStore then the mongo uri.
    --metrics                - If metrics are to be captured.
    --export-metrics         - Option to export the captured metrics. The format of the command is type;URL;key1=value1,key2=value2
                              Currently only [Prometheus Pushgateway](https://github.com/prometheus/pushgateway) is supported
                              e.g. --export-metrics "pushgateway;localhost:9091;key1=value1,key2=value2" 
    --sweep-only-refs-past-retention - Sweep only if the earliest references from all repositories are past the retention period which is govered by the max-age parameter.
                                       Boolean (Optional). Defaults to False. Only applicable for --collect-garbage
    --check-consistency-gc    - Performs a consistency check immediately after the GC.        
                                Boolean (Optional). Defaults to False. Only applicable for --collect-garbage                           
Note:

Note: When using --export-metrics the following additional jars have to be downloaded to support Prometheus Pushgatway
* [simpleclient_common-0.6.0.jar](http://central.maven.org/maven2/io/prometheus/simpleclient_common/0.6.0/simpleclient_common-0.6.0.jar) 
* [simpleclient-0.6.0.jar](http://central.maven.org/maven2/io/prometheus/simpleclient/0.6.0/simpleclient-0.6.0.jar)
* [simpleclient_pushgateway-0.6.0.jar](http://central.maven.org/maven2/io/prometheus/simpleclient_pushgateway/0.6.0/simpleclient_pushgateway-0.6.0.jar)
* [simpleclient_dropwizard-0.6.0.jar](http://central.maven.org/maven2/io/prometheus/simpleclient_dropwizard/0.6.0/simpleclient_dropwizard-0.6.0.jar)

The command to be executed when using this option is:

    $ java -classpath oak-run-*.jar:simpleclient_common-0.6.0.jar:simpleclient-0.6.0.jar:simpleclient_dropwizard-0.6.0.jar:simpleclient_pushgateway-0.6.0.jar \
        org.apache.jackrabbit.oak.run.Main \
        datastore [--check-consistency|--collect-garbage [true]] \
        [--s3ds <s3ds_config>|--fds <fds_config>|--azureds <s3ds_config>|fake-ds-path <ds_path>] \
        [--out-dir <output_path>] \
        [--work-dir <temporary_path>] \
        [--max-age <seconds>] \
        [--verbose] \
        [<store_path>|<mongo_uri>]
        [--metrics] [--export-metrics]
    
Data Store and node store configuration is mandatory.

The config files should be formatted according to the OSGi configuration admin specification

    E.g.
    cat > org.apache.jackrabbit.oak.plugins.S3DataStore.config << EOF 
    accessKey="XXXXXXXXX"
    secretKey="YYYYYY"
    s3Bucket="bucket1"
    s3Region="region1"
    EOF
    
    cat > org.apache.jackrabbit.oak.plugins.FileDataStore.config << EOF 
    path="/data/datastore"
    EOF        
    


Reset Cluster Id
---------------

Resets the cluster id generated internally. Use the following command after stopping the server

    $ java -jar oak-run-*.jar resetclusterid \
            { /path/to/oak/repository | mongodb://host:port/database | jdbc:...}

The cluster id will be removed and will be generated on next server start up.

Oak DataStore Cache Upgrade
---------------------------

Upgrades the JR2 DataStore cache by moving files to the Upload staging and the download cache of the DataStore.

    $ java -classpath oak-run-*.jar datastorecacheupgrade \
        --homeDir <home_directory> \
        --path <path> \
        --moveCache <true|false> \
        --deleteMapFile <true|false>

Unlock DocumentMK upgrade
-------------------------

See the [official documentation](http://jackrabbit.apache.org/oak/docs/nodestore/documentmk.html#unlockUpgrade).

Revisions
---------

See the [official documentation](http://jackrabbit.apache.org/oak/docs/nodestore/documentmk.html#revision-gc).

Export
------

Dumps the repository content under any repository path as json. It can also dump the blobs referred in the dumped content

    java -jar oak-run-*.jar export -p /path/in/repo /path/of/segmentstore -o /path/of/output/dir
    
This would create a json file `nodestates.json` in the output dir containing nodes content in json format. Blobs can be 
included via `-b=true` option. When enabled the blob would be stored under `blobs` directory of output dir

It support various options which can be seen by help `-h`

    $ java -jar oak-run-*.jar export -h
    Exports NodeState as json                                 
    
    
    The export command supports exporting nodes from a repository in json. It also provide options to export the blobs
      which are stored in FileDataStore format                                                                        
    
    Option                           Description                                                                       
    ------                           -----------                                                                       
    -b, --blobs [Boolean]            Export blobs also. By default blobs are not exported (default: false)             
    -d, --depth [Integer]            Max depth to include in output (default: 2147483647)                              
    -f, --filter <String>            Filter expression as json to filter out which nodes and properties are included in
                                       exported file (default: {"properties":["*", "-:childOrder"]})                   
    --filter-file <File>             Filter file which contains the filter json expression                             
    --format <String>                Export format 'json' or 'txt' (default: json)                                     
    -n, --max-child-nodes [Integer]  Maximum number of child nodes to include for a any parent (default: 2147483647)   
    -o, --out <File>                 Output directory where the exported json and blobs are stored (default: .)        
    -p, --path <String>              Repository path to export (default: /)                                            
    --pretty [Boolean]               Pretty print the json output (default: true) 
    
The command can connect to any type of Oak repository. Refer to [Oak Run NodeStore Connection][1] for details

License
-------

(see the top-level [LICENSE.txt](../LICENSE.txt) for full license details)

Collective work: Copyright 2012 The Apache Software Foundation.

Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

[1]: https://jackrabbit.apache.org/oak/docs/features/oak-run-nodestore-connection-options.html
