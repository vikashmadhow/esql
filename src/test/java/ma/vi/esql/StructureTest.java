/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql;

import ma.vi.esql.database.Structure;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.Program;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

// @todo text array testing for both postgres and mssqlserver

public class StructureTest {

  TestDatabase container = new TestDatabase();

  @Test
  public void test() {
    Structure structure = container.structure();
    Parser parser = new Parser(structure);

    Esql<?, ?> esql = parser.parse("select * from S");
    assertTrue(esql instanceof Program);
  }

//    @Test
//    public void createTables() {
//        System.out.println("Creating test tables");
//
//        // create tables
//        Database db = testConfig();
//        ConnectionProvider cp = db.forUser(user(), password());
//
//        Esql<?, ?> company = db.parse("create table com.test.Company(" +
//                                          "  {table_name: 'Companies', candidate_key: text['name', 'created_date']}, " +
//                                          "  id uuid, " +
//                                          "  name string not null {label: 'Company name', main_member: true}, " +
//                                          "  created_date date {validate: created_date < now(), required: true}, " +
//                                          "  state short {lookup: 'OPERATIONAL_STATE', required: true}, " +
//                                          "  turnover double, " +
//                                          "  males int," +
//                                          "  females int, " +
//                                          "  derived employees males + females {label: 'Workforce'}, " +
//                                          "  derived productivity employees / turnover {label: 'Productivity'}, " +
//                                          "  ceased_date date {validate: ceased_date > created_date, label: 'Ceased date'}, " +
//                                          "  primary key (id), " +
//                                          "  check (name is not null and length(name) > 0), " +
//                                          "  {auto_index: 1 + random()}, " +
//                                          "  check (ceased_date > created_date), " +
//                                          "  unique(name, created_date)" +
//                                          ")");
//
//        Esql<?, ?> product = db.parse("create table com.test.Product(" +
//                                          "  {table_name: 'Products', candidate_key: text['name', 'company_id']}, " +
//                                          "  id uuid, " +
//                                          "  name string not null {label: 'Product name', main_member: true}, " +
//                                          "  company_id uuid, " +
//                                          "  quantity_in_stock int, " +
//                                          "  unit_price float, " +
//                                          "  derived value_in_stock unit_price * quantity_in_stock {label: 'Stock value', valid: value_in_stock >= 0}, " +
//                                          "  primary key (id), " +
//                                          "  check (quantity_in_stock >= 0), " +
//                                          "  check (unit_price >= 0.0), " +
//                                          "  foreign key (company_id) references com.test.Company(id) on update set null on delete restrict, " +
//                                          "  unique(name, company_id)" +
//                                          ")");
//
//        Esql<?, ?> customer = db.parse("create table com.test.Customer(" +
//                                          "  {table_name: 'Customers'}, " +
//                                          "  id uuid, " +
//                                          "  name string not null {label: 'Customer name', main_member: true, required: true}, " +
//                                          "  address string, " +
//                                          "  email string, " +
//                                          "  phone string," +
//                                          "  dob date {label: 'Date of birth', validate: dob < now()}, " +
//                                          "  sex char {lookup: 'SEX', required: true}, " +
//                                          "  cards string[], " +
//                                          "  location int[], " +
//                                          "  primary key (id), " +
//                                          "  check (dob < now())" +
//                                          ")");
//
//        Esql<?, ?> companyCustomers = db.parse("create table com.test.CompanyCustomers(" +
//                                          "  {table_label: 'Company\\'s customers'}, " +
//                                          "  id uuid, " +
//                                          "  company_id uuid, " +
//                                          "  customer_id uuid, " +
//                                          "  primary key (id), " +
//                                          "  constraint foreign_key foreign key (company_id) references com.test.Company(id), " +
//                                          "  foreign key (customer_id) references com.test.Customer(id)" +
//                                          ")");
//
//        Esql<?, ?> orders = db.parse("create table com.test.Order(" +
//                                                "  {table_label: 'Orders'}, " +
//                                                "  id uuid, " +
//                                                "  customer_id uuid, " +
//                                                "  order_date date not null, " +
//                                                "  shipped bool, " +
//                                                "  delivered bool, " +
//                                                "  primary key (id), " +
//                                                "  foreign key (customer_id) references com.test.Customer(id)" +
//                                                ")");
//
//        Esql<?, ?> orderLines = db.parse("create table com.test.OrderLine(" +
//                                                "  {table_label: 'Order lines'}, " +
//                                                "  id uuid, " +
//                                                "  order_id uuid, " +
//                                                "  product_id uuid, " +
//                                                "  quantity int not null, " +
//                                                "  unit_price float, " +
//                                                "  primary key (id), " +
//                                                "  check (quantity > 0), " +
//                                                "  check (unit_price >= 0), " +
//                                                "  foreign key (order_id) references com.test.Order(id), " +
//                                                "  foreign key (product_id) references com.test.Product(id)" +
//                                                ")");
//
//        Esql<?, ?> withDefaults = db.parse("create table com.test.WithDefaults(" +
//                                            "id uuid default newid(), " +
//                                            "name string default 'unknown', " +
//                                            "primary key (id))");
//
//
//        Esql<?, ?> intervals = db.parse("create table com.test.Interval(" +
//                        "id uuid default newid(), " +
//                        "start date default now(), " +
//                        "duration interval, " +
//                        "primary key (id))");
//
//
//        try (EsqlConnection con = cp.esql(true)) {
//            con.exec(company);
//            con.exec(product);
//            con.exec(customer);
//            con.exec(companyCustomers);
//            con.exec(orders);
//            con.exec(orderLines);
//            con.exec(withDefaults);     // @todo error on parsing for ms sql server: recheck all
//            con.exec(intervals);
//        }
//
//        Structure s = db.structure();
//        Relation r = s.relation("com.test.Company");
//        assertEquals("com.test.Company", r.name());
//        assertEquals(10, r.fields.size());
//        assertEquals(4, r.constraints().size());
//        assertEquals(3, r.attributes().size());
//    }
//
//    private Result exec(String command, EsqlConnection con, Param... params) {
//        Esql<?, ?> esql = con.db().parse(command);
//        return con.exec(esql, params);
//    }
//
//    private void execAndShow(String command, Param... params) {
//        Database db = testConfig();
//        ConnectionProvider cp = db.forUser(user(), password());
//
//        try (EsqlConnection con = cp.esql();
//             Result rs = exec(command, con, params)) {
//            while (rs.next()) {
//                System.out.println(Maps.toString(rs.resultAttributes()));
//                System.out.println("-----------------------------------------");
//                for (int i = 1; i <= rs.columns(); i++) {
//                    System.out.println(rs.get(i));
//                }
//            }
//        }
//    }
//
//    @Test
//    public void select() {
//        execAndShow("select {table_name: 'Customers overridden', form: id::string || '-' || reverse(name)} " +
//                     "       id test_id, name, dob {required: false, label: 'Dabouche!'}, address {label: 'Address'} " +
//                     "from   com.test.Customer ex " +
//                     "where  3 <= length(name) < 25");
//    }
//
//    @Test
//    public void selectAllColumns() {
//        execAndShow("select * " +
//                     "from   com.test.Customer ex " +
//                     "where  3 <= length(name) < 25");
//    }
//
//    @Test
//    public void selectAllColumnsFromProduct() {
//        execAndShow("select p.* " +
//                     "from   com.test.Product p join com.test.Company c on p.company_id=c.id");
//    }
//
//    @Test
//    public void selectAllColumnsFromCompany() {
//        execAndShow("select * from com.test.Company co");
//    }
//
//    @Test
//    public void selectAllColumnsFromCompanyAndProducts() {
//        execAndShow("select c.* " +
//                     "from   com.test.Product p " +
//                     "join   com.test.Company c on p.company_id=c.id");
//    }
//
//    @Test
//    public void selectAllColumnsFromJoin() {
//        execAndShow("select {table_name: 'Company products', " +
//                    "         description: c.name || ': ' || p.name} " +
//                    "        distinct c.*, p.* " +
//                    "from   com.test.Product p join com.test.Company c on p.company_id=c.id");
//    }
//
//    @Test
//    public void selectAllColumnsFromCrossAndSelectJoin() {
//        execAndShow("select {table_name: 'Company products', " +
//                "         description: c.name || ': ' || p.name} " +
//                "        distinct c.*, p.*, cust.* " +
//                "from   com.test.Product p join com.test.Company c on p.company_id=c.id " +
//                "       cross (select * from com.test.Customer c) cust");
//    }
//
//    @Test
//    public void selectFromJoinOnDerivedFields() {
//        execAndShow("select {table_name: 'Company products', " +
//                "         description: c.name || ': ' || p.name} " +
//                "        distinct c.*, p.*, cust.* " +
//                "from   com.test.Product p join com.test.Company c on p.value_in_stock=c.productivity / 2 " +
//                "       cross (select * from com.test.Customer c) cust");
//    }
//
//    @Test
//    public void selectFromDerivedColumns() {
//        execAndShow("select employees, productivity from com.test.Company c");
//    }
//
//    @Test
//    public void insert() {
//        execAndShow("insert into com.test.Customer " +
//                        "values(newid(), 'Sarabhai', 'Milouchai', 'sarabhai@superevil.com', " +
//                        "'358915151', d'1200-10-01' ,'X', string['x'], int[1,2,3])");
//    }
//
//    @Test
//    public void insertWithReturning() {
//        execAndShow("insert into com.test.Customer " +
//                        "values(newid(), 'Sarabhai', 'Milouchai', 'sarabhai@superevil.com', " +
//                        "'358915151', d'1200-10-01' ,'X', string['a','b','c'], int[1,2,3]) " +
//                        "returning id, name, sex || '(this is so great)' sex, 'This is perfect'");
//    }
//
//    @Test
//    public void insertFromSelectWithReturnAll() {
//        execAndShow("insert into com.test.Customer " +
//                             "select newid(), name, address, email, phone, dob, sex, cards, location " +
//                             "from com.test.Customer c returning *");
//    }
//
//    @Test
//    public void insertFromSelectWithExplicitFieldsAndReturn() {
//        execAndShow("insert into com.test.Customer(id, name, sex) " +
//                             "select newid(), name, sex " +
//                             "from com.test.Customer c " +
//                             "returning id {label: 'New id', validate: id is not null}, sex, Customer.name || ' added'");
//    }
//
//    @Test
//    public void insertFromSelect() {
//        execAndShow("insert into com.test.Customer(id, name, sex) " +
//                        "select newid(), name, 'D' {test: true} " +
//                        "from com.test.Customer c " +
//                        "returning id {label: 'New id', validate: id is not null}, sex, Customer.name || ' added'");
//    }
//
//    @Test
//    public void deleteWhereSexIsD() {
//        execAndShow("delete from com.test.Customer c where sex='D'");
//    }
//
//    @Test
//    public void deleteWhereSexIsDReturningAll() {
//        execAndShow("delete from com.test.Customer c where sex='D' returning *");
//    }
//
//    @Test
//    public void deleteWhereSexIsDReturningAllFromJoin() {
//        execAndShow("delete com.test.Customer c " +
//                             "using com.test.Company co join com.test.Product p on p.company_id=co.id " +
//                             "where c.sex='X' returning c.*");
//    }
//
//    @Test
//    public void insertDefaultValues() {
//        execAndShow("insert into com.test.WithDefaults default values");
//    }
//
//    @Test
//    public void insertDefaultId() {
//        execAndShow("insert into com.test.WithDefaults values(default, 'test') returning *");
//    }
//
//    @Test
//    public void insertNames() {
//        execAndShow("insert into com.test.WithDefaults(name) values('test1'), ('test2') returning *");
//    }
//
//    @Test
//    public void insertCompanies() {
//        execAndShow("insert into com.test.Company " +
//                            "values(newid(), 'company_" + random(4) + "', d'2012-10-30', 3, 3434333437.3, 23,45, now()), " +
//                                  "(newid(), 'company_" + random(4) + "', d'2012-10-30', 3, 3434333437.3, 23,45, now()) " +
//                            "returning *");
//    }
//
//    @Test
//    public void updateCustomers() {
//        execAndShow("update com.test.Customer c  " +
//                        "set sex='U', name=name || ' touché' " +
//                        "where sex is not null " +
//                        "returning *");
//    }
//
//    @Test
//    public void updateFromJoin() {
//        execAndShow("update com.test.Customer c " +
//                        "set dob=d'2013-01-02' " +
//                        "using com.test.Company co join com.test.Product p on p.company_id=co.id " +
//                        "where c.sex in ('X', 'U') returning c.*");
//    }
//
//    @Test
//    public void updateFromJoinWithInterval() {
//        execAndShow("update com.test.Customer c " +
//                        "set dob=incdate(dob, i'1y,1d,-3mon') " +
//                        "using com.test.Company co join com.test.Product p on p.company_id=co.id " +
//                        "where c.sex in ('X', 'U') returning c.*");
//    }
//
//    @Test
//    public void insertSomeIntervals() {
//        execAndShow("insert into com.test.Interval(start, duration)" +
//                        "values(d'2010-1-1', i'1mon'), " +
//                              "(d'2010-2-1', i'-2y,1mon,1w'), " +
//                              "(d'2010-3-1', i'-4y,1mon,1d,1h'), " +
//                              "(d'2010-3-1', i'-4y,1mon,1d,1h,23m,45s,233ms') " +
//                        "returning *");
//    }
//
//    @Test
//    public void selectIntervals() {
//        execAndShow("select * from com.test.Interval i");
//    }
//
//    @Test
//    public void selectWithIntervals() {
//        execAndShow("select c.*, i.* " +
//                        "from com.test.Customer c cross com.test.Interval i " +
//                        "where i.start <= c.dob <= incdate(i.start, i.duration)");
//    }
//
//    @Test
//    public void unionSelect() {
//        execAndShow("select * from com.test.Customer c where sex='U' union " +
//                    "select * from com.test.Customer c where sex='X' union " +
//                    "select * from com.test.Customer c where sex='N'");
//    }
//
//    @Test
//    public void unionAllSelect() {
//        execAndShow("select * from com.test.Customer c where sex='U' union all " +
//                    "select * from com.test.Customer c where sex='X' union " +
//                    "select * from com.test.Customer c where sex='N'");
//    }
//
//    @Test
//    public void unionSelectWithMetadata() {
//        execAndShow("select name {a: 'a'} from com.test.Customer c where sex='U' union " +
//                    "select c.name {b: 'b' || '3', c: id} from com.test.Customer c where sex='X' union all " +
//                    "select c.name from com.test.Customer c where sex='N' ");
//    }
//
//    @Test
//    public void unionAndExceptSelectWithMetadata() {
//        execAndShow("select name {a: 'a'} from com.test.Customer c where sex='U' union all " +
//                    "select c.name {b: 'b' || '3', c: id} from com.test.Customer c where sex='X' except " +
//                    "select c.name from com.test.Customer c where sex='N' ");
//    }
//
//    @Test
//    public void intersectAndExceptSelectWithMetadata() {
//        execAndShow("select name {a: 'a'} from com.test.Customer c where sex='U' intersect " +
//                    "select c.name {b: 'b' || '3', c: id} from com.test.Customer c where sex='X' except " +
//                    "select c.name from com.test.Customer c where sex='N' ");
//    }
//
//    @Test
//    public void with() {
//        execAndShow("with w1 (select * from com.test.Customer c) " +
//                        "select c.name, w.sex from com.test.Customer c join w1 w on c.id=w.id where w.sex='U'");
//    }
//
//    @Test
//    public void withMultiCte() {
//        execAndShow("with w1 (select * from com.test.Customer c), " +
//                        "w2 (n, s) (select c.name, w.sex from com.test.Customer c join w1 w on c.id=w.id where w.sex='U')," +
//                        "w3 (select * from com.test.Company co) " +
//                        "select w.n, w.s from w2 w cross w1 x where w.s in ('X')");
//    }
//
//    @Test
//    public void withMultiCteInsert() {
//        execAndShow("with w1 (select * from com.test.Customer c order by id offset 100 limit 10), " +
//                        "w2 (n, s) (select c.name, w.sex from com.test.Customer c join w1 w on c.id=w.id where w.sex='U' order by w.id limit 10)," +
//                        "w3 (select * from com.test.Company co) " +
//                        "insert into com.test.Customer c (id, name, sex) select newid(), w.n, w.s from w2 w cross w1 x returning *");
//    }
//
//    // Works only in Postgresql. SQL server supports selects only in CTE.
//    @Test
//    public void withInsertFromDelete() {
//        if (testConfig().config().postgresql()) {
//            execAndShow("with w1 (delete from com.test.Customer c returning *) " +
//                            "insert into com.test.Customer c select * from w1 w returning *");
//        }
//    }
//
//    @Test
//    public void selectWithParams() {
//        execAndShow("select {table_name: 'Customers overridden', form: id::string || '-' || reverse(name)} " +
//                        "        id test_id, name, dob {required: false, label: 'Dabouche!'}, address {label: 'Address'} " +
//                        "from   com.test.Customer ex " +
//                        "where  :minAge <= length(name) < :maxAge",
//                        Param.of("minAge", 3),
//                        Param.of("maxAge", 25));
//    }
//
//    @Test
//    public void selectWithMultiParams() {
//        execAndShow("select {table_name: :tableName, form: id::string || '-' || reverse(name)} " +
//                        "        id test_id, name, dob {required: false, label: :tableName || ' Dabouche!'}, address {label: 'Address'} " +
//                        "from   com.test.Customer ex " +
//                        "where  :minAge <= length(name) < :maxAge and dob=:dob",
//                        Param.of("tableName", "Customer's name"),
//                        Param.of("minAge", 3),
//                        Param.of("maxAge", 25),
//                        Param.of("dob", Dates.toSqlDate(Dates.of(2012, 10, 10))));
//    }
//
//    @Test
//    public void arrayInsertAndRead() {
//        Database db = testConfig();
//        ConnectionProvider cp = db.forUser(user(), password());
//        try (EsqlConnection con = cp.esql()) {
//
//            exec("delete from com.test.Customer c", con);
//            exec("insert into com.test.Customer values " +
//                 "(" +
//                 "  newid(), 'Monty Python', 'A test address', 'a@b.c', '123-000-23', d'1980-01-02', 'M', " +
//                 "  string['a','b','cde'], int[3,4,2,1,6]" +
//                 "), " +
//                 "(" +
//                 "  newid(), 'Jane miton', 'Another test address', 'ef@b.c', '543-064-89', d'1956-11-12', 'F', " +
//                 "  string['3232','23235cb','fddfcde','alpha'], int[4,1]" +
//                 ")", con);
//
//            try(Result rs = exec("select name, cards, location from com.test.Customer c where " +
//                                 "name in ('Monty Python', 'Jane miton')", con)) {
//                while(rs.next()) {
//                    String name = (String) rs.get(1).value;
//                    if (name.equals("Monty Python")) {
//                        assertArrayEquals(new String[]{"a", "b", "cde"}, (String[]) rs.get(2).value);
//                        assertArrayEquals(new Integer[]{3, 4, 2, 1, 6}, (Integer[]) rs.get(3).value);
//                    } else {
//                        assertArrayEquals(new String[]{"3232","23235cb","fddfcde","alpha"}, (String[]) rs.get(2).value);
//                        assertArrayEquals(new Integer[]{4, 1}, (Integer[]) rs.get(3).value);
//                    }
//                }
//            }
//        }
//    }
//
//    @Test
//    public void renameTable() {
//        Database db = testConfig();
//        Structure s = db.structure();
//        assertNotNull(s.relation("com.test.Company"));
//
//        execAndShow("alter table com.test.Company rename to Coumpani");
//        assertNull(s.relation("com.test.Company"));
//        assertNotNull(s.relation("com.test.Coumpani"));
//
//        execAndShow("alter table com.test.Coumpani rename to Company");
//        assertNull(s.relation("com.test.Coumpani"));
//        assertNotNull(s.relation("com.test.Company"));
//    }

//    @Test public void addColumnsTable() {
//        Database db = testConfig();
//        Structure s = db.structure();
//        assertNotNull(s.relation("com.test.Company"));
//
//        execAndShow("alter table com.test.Company rename to Coumpani");
//        assertNull(s.relation("com.test.Company"));
//        assertNotNull(s.relation("com.test.Coumpani"));
//
//        execAndShow("alter table com.test.Coumpani rename to Company");
//        assertNull(s.relation("com.test.Coumpani"));
//        assertNotNull(s.relation("com.test.Company"));
//    }

//    @Test public void dropCustomerTable() throws Exception {
//        Database db = testConfig();
//        ConnectionProvider cp = db.forUser(user(), password());
//
//        Structure s = db.structure();
//        assertNotNull(s.relation("com.test.Customer"));
//        assertNotNull(s.relation("com.test.Order"));
//        assertNotNull(s.relation("com.test.OrderLine"));
//
//        try (EsqlConnection con = cp.esql()) {
//            con.exec(db.parse("drop table com.test.Customer"));
//        }
//
//        assertNull(s.relation("com.test.Customer"));
//        assertNull(s.relation("com.test.Order"));
//        assertNull(s.relation("com.test.OrderLine"));
//    }

//
//    @Test public void dropTables() throws Exception {
//        // drop tables
//        System.out.println("Dropping test tables");
//
//        Database db = testConfig();
//        ConnectionProvider cp = db.forUser(user(), password());
//
//        try (EsqlConnection con = cp.esql()) {
//            con.exec(db.parse("drop table com.test.OrderLine"));
//            con.exec(db.parse("drop table com.test.Order"));
//            con.exec(db.parse("drop table com.test.Product"));
//            con.exec(db.parse("drop table com.test.CompanyCustomers"));
//            con.exec(db.parse("drop table com.test.Company"));
//            con.exec(db.parse("drop table com.test.Customer"));
//            con.exec(db.parse("drop table com.test.Interval"));
//        }
//
//        Structure s = db.structure();
//        assertNull(s.relation("com.test.Company"));
//        assertNull(s.relation("com.test.Product"));
//        assertNull(s.relation("com.test.Customer"));
//        assertNull(s.relation("com.test.CompanyCustomers"));
//        assertNull(s.relation("com.test.Order"));
//        assertNull(s.relation("com.test.OrderLine"));
//        assertNull(s.relation("com.test.Interval"));
//    }

  // test array
  // test merge
  // test lookups
  // test bulk insert
  // alter table
  // create, alter and drop index
  // create, alter and drop sequence
  // access sequence
  // with recursive queries
  // window functions
  // grouping sets
  // parsing of parameters (include an ESQL expression as a parameter)

//    protected synchronized Database testConfig() {
//        if (_db == null) {
//            Configuration c = ConfigurationTest.testPostgresConfig();
//            _db = DatabaseFactory.get(c);
//        }
//        return _db;
//    }
//
//    protected String user() { return "cso_user"; }
//    protected String password() { return  "cso_user"; }
//
//    protected static volatile Database _db;
}