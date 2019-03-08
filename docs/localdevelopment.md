
Technologies
------------
Service built on:
  - [Spring Framework](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/) Server framework
  - [Thymeleaf](https://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.html) Templating language
  - [CSS Bootstrap](https://getbootstrap.com/docs/3.4/css/) Front end formatting
  - [PostgreSQL](https://www.postgresql.org/) Database (developed on 10.7)
  - [Maven](https://maven.apache.org/index.html) Build/dependency management


Local Development
-----------------
Install Postgresql Server (commands vary)

**Fedora Linux 29:**
```
$ sudo dnf install postgresql postgresql-contrib postgresql-server     # Install packages
$ sudo postgresql-setup --initdb                # Initialize database
```

**Ubuntu Linux 18.04:**
```
$ sudo apt -y install postgresql postgresql-contrib
$ sudo -i -u postgres

```
For Ubuntu, skip to the section on creating a database user below
On Fedora 29, enable passwords for local connections by editing the /var/lib/pgsql/data/pg_hba.conf file:
```
Change the line:
host    all             all             127.0.0.1/32            ident
To:
host    all             all             127.0.0.1/32            md5

```
Then start and enable the service:
```
$ sudo systemctl enable postgresql              # Enable service
$ sudo systemctl start postgresql               # Start service
```


By default, Postgres does not include case-insensitive searching. The commands below add the
citext extension, which will enable that. Note that columns have to have the `citext` type.

Create a Postgresql database user "kumoreg" and database "registration" after installing server:
Note: for Ubuntu installs, skip the "su" command
Note: the createdb parameter -O is the letter "oh" and not a "zero"

```
$ su - postgres
$ createuser --interactive -P kumoreg
    Enter password for new role:
    Enter it again:
    Shall the new role be a superuser? (y/n) n
    Shall the new role be allowed to create databases? (y/n) n
    Shall the new role be allowed to create more new roles? (y/n) n

$ createdb -O kumoreg registration
$ psql registration -c "CREATE EXTENSION citext;"
```

Handy Postgres Commands
-----------------------
```
# Connect with command line SQL client:
psql -h 127.0.0.1 -U kumoreg registration
```
- \d - List tables
- \d <tablename> - Describe a table
- \l - List databases
- \q - Quit


Build and Run the Server with Maven:
------------------------------------
```
mvn spring-boot:run
```


Architecture Notes
------------------
- Server needs to be able to run both in "the cloud" (Typically Amazon Web Services)
and on a local server because there may not be internet connectivity during the
convention.
  - This means that timezones need to be handled property, but support for timezones
  other than Pacific has so far been unnecessary
  - Due to the printing flow, printing directly from the server isn't really possible
  when running on a cloud server. This is fine, since AWS is used only for
  training/test data.
- Some of the computers at con are very low resolution, try to make screens look
  good when running below 1300x700 pixels.
- Working with mobile devices is nice, since during training many people use
  tablets or phones.
- This service is for attendee management only, staff check ins is handled by a
  different service.
