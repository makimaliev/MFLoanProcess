package kg.gov.mf.loan.process.job;

import kg.gov.mf.loan.admin.org.model.*;
import kg.gov.mf.loan.admin.org.service.*;
import kg.gov.mf.loan.admin.sys.model.User;
import kg.gov.mf.loan.admin.sys.service.UserService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;

@Transactional
@Component
public class Migration1Job implements Job{

    @Autowired
    PersonService personService;

    @Autowired
    StaffService staffService;

    @Autowired
    IdentityDocGivenByService identityDocGivenByService;

    @Autowired
    IdentityDocTypeService identityDocTypeService;

    @Autowired
    VillageService villageService;

    @Autowired
    AokmotuService aokmotuService;

    @Autowired
    OrgFormService orgFormService;

    @Autowired
    UserService userService;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    PositionService positionService;

    @Autowired
    RegionService regionService;

    @Autowired
    DistrictService districtService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        Connection connection = this.getSourceConnection();


        boolean done = true;

        // DONE

        boolean orgFormMigrationDone = done;
        if(!orgFormMigrationDone) orgFormMigrationDone = this.orgFormMigrate(connection);

        boolean regionMigrationDone = done;
        if(!regionMigrationDone) regionMigrationDone = this.regionMigrate(connection);

        boolean districtMigrationDone = done;
        if(!districtMigrationDone) districtMigrationDone = this.districtMigrate(connection);


        boolean aokmotuMigrationDone = done;
        if(!aokmotuMigrationDone) aokmotuMigrationDone = this.aokmotuMigrate(connection);

        boolean villageMigrationDone = done;
        if(!villageMigrationDone) villageMigrationDone = this.villageMigrate(connection);

        boolean idDocTypeMigrationDone = done;
        if(!idDocTypeMigrationDone) idDocTypeMigrationDone = this.idDocTypeMigrate(connection);

        boolean idDocGivenByMigrationDone = done;
        if(!idDocGivenByMigrationDone) idDocGivenByMigrationDone = this.idDocGivenByMigrate(connection); // CREATE NEW

        boolean gaubkMigrationDone = done;
        if(!gaubkMigrationDone) gaubkMigrationDone = this.gaubkMigrate(connection); // CREATE NEW

        boolean departmentMigrationDone = done;
        if(!departmentMigrationDone) departmentMigrationDone = this.departmentMigrate(connection);

        boolean positionMigrationDone = done;
        if(!positionMigrationDone) positionMigrationDone = this.positionMigrate(connection);

       boolean userMigrationDone = done;
        if(!userMigrationDone) userMigrationDone = this.migrateUsers(connection);

        System.out.println(" OrgForm migration process == "+orgFormMigrationDone);
        System.out.println(" Region migration process == "+regionMigrationDone);
        System.out.println(" District migration process == "+districtMigrationDone);
        System.out.println(" Aokmotu migration process == "+aokmotuMigrationDone);
        System.out.println(" Village migration process == "+villageMigrationDone);
        System.out.println(" ID doc type migration process == "+idDocTypeMigrationDone);
        System.out.println(" GAUBK migration process == "+gaubkMigrationDone);
        System.out.println(" Department migration process == "+departmentMigrationDone);
        System.out.println(" Position migration process == "+positionMigrationDone);
        System.out.println(" User migration process == "+userMigrationDone);
    }






    private boolean migrateUsers(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("SELECT *,\n" +
                            "  (SELECT system_type.type_name FROM system_type WHERE system_type.group_id = 11 AND system_type.type_id = users.type) AS position_name,\n" +
                            "  (SELECT phone.number FROM phone WHERE phone.user_id = users.id AND phone.contact_type = 1) AS phone_number\n" +
                            "FROM users,profile,address WHERE  users.id = profile.user_id AND address.contact_type = 1 AND address.user_id = users.id\n" +
                            "ORDER BY users.id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            User user = new User();

                            user.setUsername(rs.getString("login"));
                            user.setPassword(rs.getString("password"));
                            user.setEnabled(rs.getShort("status")==1 ? true : false);
                            userService.create(user);

                            if(user.isEnabled())
                            {
                                Staff staff = new Staff();

                                staff.setEnabled(true);
                                staff.setName(rs.getString("last_name")+" "+rs.getString("first_name")+ " "+rs.getString("middle_name"));
                                staff.setOrganization(this.organizationService.findById(1));
                                Department department = this.departmentService.findById(rs.getInt("department"));
                                staff.setDepartment(department);

                                for (Position position:department.getPosition())
                                {
                                    if(position.getName().contains(rs.getString("position_name")))
                                        staff.setPosition(position);
                                }

                                if(staff.getPosition()==null)
                                    staff.setPosition(this.positionService.findById(1));

                                Contact contact = new Contact();
                                contact.setName(rs.getString("phone_number"));

                                Address address = new Address();

                                Region region = new Region();
                                if(this.regionService.findByCode(String.valueOf(rs.getInt("region")))!=null)
                                    region = this.regionService.findByCode(String.valueOf(rs.getInt("region")));
                                else region = this.regionService.findByCode("2");

                                address.setRegion(region); // Bishkek

                                District district = new District();


                                if(this.districtService.findById(rs.getInt("district")==0 ? 1 : rs.getInt("district"))!= null)
                                    district = this.districtService.findById(rs.getInt("district")==0 ? 1 : rs.getInt("district"));
                                else district = this.districtService.findById(48);
                                address.setDistrict(district); // Pervomayskiy raion
                                address.setLine(rs.getString("address_line1"));

                                IdentityDoc identityDoc = new IdentityDoc();
                                identityDoc.setIdentityDocGivenBy(identityDocGivenByService.findById(1)); //MKK
                                identityDoc.setIdentityDocType(identityDocTypeService.findById(2)); // Passport
                                identityDoc.setEnabled(true);
                                identityDoc.setName(" паспортные данные сотрудника");
                                identityDoc.setPin("инн сотрудника");
                                identityDoc.setNumber("номер Ид.документа");

                                IdentityDocDetails identityDocDetails = new IdentityDocDetails();
                                identityDocDetails.setFirstname(rs.getString("first_name"));
                                identityDocDetails.setLastname(rs.getString("last_name"));
                                identityDocDetails.setFullname(staff.getName());
                                identityDocDetails.setMidname(rs.getString("middle_name"));

                                identityDoc.setIdentityDocDetails(identityDocDetails);

                                Person person = new Person();



                                person.setName(staff.getName());
                                person.setDescription("Примечание сотрудника");
                                person.setEnabled(true);
                                person.setContact(contact);
                                person.setAddress(address);
                                person.setIdentityDoc(identityDoc);


                                this.personService.create(person);

                                staff.setPerson(person);

                                this.staffService.create(staff);
                            }
                        }

                        migrationSuccess = true;
                        rs.close();
                        st.close();
                    }
                }
                catch (SQLException ex)
                {
                    System.out.println("Connection Failed! Check output console");
                    ex.printStackTrace();
                    return migrationSuccess;
                }

            }
            else
            {
                System.out.println("Failed to make connection!");
            }
        }
        catch(Exception ex)
        {
            System.out.println(" Error in User migration "+ex);
        }

        return migrationSuccess;
    }



    private boolean positionMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("SELECT\n" +
                            "  (SELECT system_type.type_name FROM system_type WHERE system_type.group_id = 10 AND system_type.type_id = users.department) AS department_name,\n" +
                            "  (SELECT system_type.type_name FROM system_type WHERE system_type.group_id = 11 AND system_type.type_id = users.type) AS position_name,\n" +
                            "  users.department\n" +
                            "FROM users WHERE users.status = 1 ORDER BY department_name,position_name");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                                Position position = new Position();

                                Department department = this.departmentService.findById(rs.getInt("department"));
                                position.setDepartment(department);
                                position.setName(rs.getString("position_name")+"("+department.getName()+")");

                                positionService.create(position);
                        }

                        migrationSuccess = true;
                        rs.close();
                        st.close();
                    }
                }
                catch (SQLException ex)
                {
                    System.out.println("Connection Failed! Check output console");
                    ex.printStackTrace();
                    return migrationSuccess;
                }

            }
            else
            {
                System.out.println("Failed to make connection!");
            }
        }
        catch(Exception ex)
        {
            System.out.println(" Error in User migration "+ex);
        }

        return migrationSuccess;
    }


    private boolean departmentMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 10 order by system_type.type_id");
                    if(rs != null)
                    {
                        Organization gaubk = organizationService.findById(1);

                        while (rs.next())
                        {
                            Department department = new Department();

                            department.setName(rs.getString("type_name"));
                            department.setEnabled(rs.getShort("status")==1 ? true : false);
                            department.setOrganization(gaubk);

                            departmentService.create(department);
                        }

                        migrationSuccess = true;
                        rs.close();
                        st.close();
                    }
                }
                catch (SQLException ex)
                {
                    System.out.println("Connection Failed! Check output console");
                    ex.printStackTrace();
                    return migrationSuccess;
                }

            }
            else
            {
                System.out.println("Failed to make connection!");
            }
        }
        catch(Exception ex)
        {
            System.out.println(" Error in User migration "+ex);
        }

        return migrationSuccess;
    }

    private boolean gaubkMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Organization gaubk = new Organization();

                    Contact contact = new Contact();
                    contact.setName("0312 666033");

                    Address address = new Address();
                    address.setRegion(this.regionService.findByCode("2")); // Bishkek
                    address.setDistrict(this.districtService.findById(48)); // Pervomayskiy raion
                    address.setLine("б.Эркиндик 58-а");

                    IdentityDoc identityDoc = new IdentityDoc();
                    identityDoc.setIdentityDocGivenBy(identityDocGivenByService.findById(2)); //MinJust
                    identityDoc.setIdentityDocType(identityDocTypeService.findById(1)); // Svid. o reg.
                    identityDoc.setEnabled(true);
                    identityDoc.setName(" данные ГАУБК");
                    identityDoc.setPin("inn");
                    identityDoc.setNumber("number");

                    IdentityDocDetails identityDocDetails = new IdentityDocDetails();
                    identityDocDetails.setFirstname("");
                    identityDocDetails.setLastname("");
                    identityDocDetails.setLastname("");
                    identityDocDetails.setFullname("");
                    identityDocDetails.setMidname("");

                    identityDoc.setIdentityDocDetails(identityDocDetails);



                    gaubk.setName("ГАУБК при МФ КР");
                    gaubk.setDescription("Государственное агентство по управлению бюджетными кредитами при МФ КР");
                    gaubk.setEnabled(true);
                    gaubk.setContact(contact);
                    gaubk.setAddress(address);
                    gaubk.setIdentityDoc(identityDoc);
                    gaubk.setOrgForm(this.orgFormService.findById(1)); // Jur

                    this.organizationService.create(gaubk);
                    migrationSuccess = true;
                }
                catch (Exception ex)
                {
                    System.out.println("Connection Failed! Check output console");
                    ex.printStackTrace();
                    return migrationSuccess;
                }

            }
            else
            {
                System.out.println("Failed to make connection!");
            }
        }
        catch(Exception ex)
        {
            System.out.println(" Error in User migration "+ex);
        }

        return migrationSuccess;
    }


    private boolean idDocGivenByMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    IdentityDocGivenBy identityDocGivenBy1 = new IdentityDocGivenBy();

                    identityDocGivenBy1.setName("МКК");
                    identityDocGivenBy1.setEnabled(true);

                    identityDocGivenByService.create(identityDocGivenBy1);

                    IdentityDocGivenBy identityDocGivenBy2 = new IdentityDocGivenBy();

                    identityDocGivenBy2.setName("МинЮст");
                    identityDocGivenBy2.setEnabled(true);

                    identityDocGivenByService.create(identityDocGivenBy2);

                    migrationSuccess = true;
                }
                catch (Exception ex)
                {
                    System.out.println("Connection Failed! Check output console");
                    ex.printStackTrace();
                    return migrationSuccess;
                }

            }
            else
            {
                System.out.println("Failed to make connection!");
            }
        }
        catch(Exception ex)
        {
            System.out.println(" Error in User migration "+ex);
        }

        return migrationSuccess;
    }

    private boolean idDocTypeMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 18 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            IdentityDocType identityDocType = new IdentityDocType();

                            identityDocType.setName(rs.getString("type_name"));
                            identityDocType.setEnabled(rs.getShort("status")==1 ? true : false);

                            identityDocTypeService.create(identityDocType);
                        }

                        migrationSuccess = true;
                        rs.close();
                        st.close();
                    }
                }
                catch (SQLException ex)
                {
                    System.out.println("Connection Failed! Check output console");
                    ex.printStackTrace();
                    return migrationSuccess;
                }

            }
            else
            {
                System.out.println("Failed to make connection!");
            }
        }
        catch(Exception ex)
        {
            System.out.println(" Error in User migration "+ex);
        }

        return migrationSuccess;
    }

    private boolean villageMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select\n" +
                            "  *,\n" +
                            "  (select distinct aokmotu.title from aokmotu where\n" +
                            "  aokmotu.aokmotu = selo.aokmotu AND\n" +
                            "    aokmotu.district = selo.district AND\n" +
                            "    aokmotu.region = selo.region\n" +
                            "    limit 1\n" +
                            "  ) as aokmotu_title\n" +
                            "from selo");
                    if(rs != null)
                    {
                        int count =0;
                        while (rs.next())
                        {
                            count++;
                            try
                            {
                                Village village  = new Village();

                                village.setName(rs.getString("title"));
                                village.setCode((String.valueOf(rs.getInt("selo_code"))));

                                Aokmotu aokmotu = new Aokmotu();

                                if(rs.getString("aokmotu_title")!=null)
                                {
                                    aokmotu = this.aokmotuService.findByName(rs.getString("aokmotu_title"));
                                }
                                else aokmotu = this.aokmotuService.findById(1);

                                village.setAokmotu(aokmotu);

                                villageService.create(village);

                            }
                            catch (Exception ex)
                            {
                                System.out.println(ex);
                                System.out.println(count);
                            }
                        }

                        migrationSuccess = true;
                        rs.close();
                        st.close();
                    }
                }
                catch (SQLException ex)
                {
                    System.out.println("Connection Failed! Check output console");
                    ex.printStackTrace();
                    return migrationSuccess;
                }

            }
            else
            {
                System.out.println("Failed to make connection!");
            }
        }
        catch(Exception ex)
        {
            System.out.println(" Error in User migration "+ex);
        }

        return migrationSuccess;
    }

    private boolean aokmotuMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select\n" +
                            "  *,\n" +
                            "  (\n" +
                            "    SELECT DISTINCT district_codes.district_id from district_codes\n" +
                            "    where district_codes.district_code = aokmotu.district AND\n" +
                            "        district_codes.region_code = aokmotu.region limit 1\n" +
                            "     ) as district_id\n" +
                            "from aokmotu");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            Aokmotu aokmotu = new Aokmotu();

                            aokmotu.setName(rs.getString("title"));

                            District district = this.districtService.findById(rs.getInt("district_id"));

                            if(rs.getInt("district_id")==11) district = this.districtService.findById(63);
                            if(rs.getInt("district_id")==15) district = this.districtService.findById(30);
                            if(rs.getInt("district_id")==47) district = this.districtService.findById(36);
                            if(rs.getInt("district_id")==18) district = this.districtService.findById(35);
                            if(rs.getInt("district_id")==19) district = this.districtService.findById(52);

                            aokmotu.setDistrict(district );
                            aokmotu.setCode(String.valueOf(rs.getInt("aokmotu")));

                            aokmotuService.create(aokmotu);
                        }

                        migrationSuccess = true;
                        rs.close();
                        st.close();
                    }
                }
                catch (SQLException ex)
                {
                    System.out.println("Connection Failed! Check output console");
                    ex.printStackTrace();
                    return migrationSuccess;
                }

            }
            else
            {
                System.out.println("Failed to make connection!");
            }
        }
        catch(Exception ex)
        {
            System.out.println(" Error in User migration "+ex);
        }

        return migrationSuccess;
    }

    private boolean districtMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 13 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            District district = new District();

                            district.setName(rs.getString("type_name"));
                            district.setCode(String.valueOf(rs.getLong("type_id")));

                            if(rs.getInt("details_id")>0 )
                                if(rs.getInt("details_id")<10 )
                                    district.setRegion(this.regionService.findByCode(String.valueOf(rs.getInt("details_id"))));
                                else district.setRegion(this.regionService.findByCode("2"));

                            if(rs.getInt("details_id")==0 ) district.setRegion(this.regionService.findByCode("9"));


                            districtService.create(district);
                        }

                        migrationSuccess = true;
                        rs.close();
                        st.close();
                    }
                }
                catch (SQLException ex)
                {
                    System.out.println("Connection Failed! Check output console");
                    ex.printStackTrace();
                    return migrationSuccess;
                }

            }
            else
            {
                System.out.println("Failed to make connection!");
            }
        }
        catch(Exception ex)
        {
            System.out.println(" Error in User migration "+ex);
        }

        return migrationSuccess;
    }

    private boolean regionMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 12 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            Region region = new Region();

                            region.setName(rs.getString("type_name"));
                            region.setCode(String.valueOf(rs.getLong("type_id")));

                            regionService.create(region);
                        }

                        migrationSuccess = true;
                        rs.close();
                        st.close();
                    }
                }
                catch (SQLException ex)
                {
                    System.out.println("Connection Failed! Check output console");
                    ex.printStackTrace();
                    return migrationSuccess;
                }

            }
            else
            {
                System.out.println("Failed to make connection!");
            }
        }
        catch(Exception ex)
        {
            System.out.println(" Error in User migration "+ex);
        }

        return migrationSuccess;
    }

    private boolean orgFormMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 20 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            OrgForm orgForm = new OrgForm();

                            orgForm.setName(rs.getString("type_name"));
                            orgForm.setEnabled(rs.getShort("status")==1 ? true : false);

                            orgFormService.create(orgForm);
                        }

                        migrationSuccess = true;
                        rs.close();
                        st.close();
                    }
                }
                catch (SQLException ex)
                {
                    System.out.println("Connection Failed! Check output console");
                    ex.printStackTrace();
                    return migrationSuccess;
                }

            }
            else
            {
                System.out.println("Failed to make connection!");
            }
        }
        catch(Exception ex)
        {
            System.out.println(" Error in OrgForm migration "+ex);
        }

        return migrationSuccess;
    }

    private Connection getSourceConnection()
    {
        Connection connection = null;
        try
        {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
            e.printStackTrace();
            return null;

        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        try {

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://31.186.54.58:5432/migration2", "postgres",
                    "armad27raptor");

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return null;

        }

        return connection;
    }
}
