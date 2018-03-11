package kg.gov.mf.loan.process.job;

import kg.gov.mf.loan.admin.org.model.*;
import kg.gov.mf.loan.admin.org.service.*;
import kg.gov.mf.loan.admin.sys.model.User;
import kg.gov.mf.loan.admin.sys.service.UserService;
import kg.gov.mf.loan.manage.model.collateral.InspectionResultType;
import kg.gov.mf.loan.manage.model.collateral.ItemType;
import kg.gov.mf.loan.manage.model.collateral.QuantityType;
import kg.gov.mf.loan.manage.model.debtor.Debtor;
import kg.gov.mf.loan.manage.model.debtor.DebtorType;
import kg.gov.mf.loan.manage.model.debtor.Owner;
import kg.gov.mf.loan.manage.model.debtor.WorkSector;
import kg.gov.mf.loan.manage.model.loan.LoanState;
import kg.gov.mf.loan.manage.model.loan.LoanType;
import kg.gov.mf.loan.manage.model.loan.PaymentType;
import kg.gov.mf.loan.manage.model.order.CreditOrder;
import kg.gov.mf.loan.manage.model.order.CreditOrderState;
import kg.gov.mf.loan.manage.model.order.CreditOrderType;
import kg.gov.mf.loan.manage.model.orderterm.OrderTermCurrency;
import kg.gov.mf.loan.manage.model.orderterm.OrderTermDaysMethod;
import kg.gov.mf.loan.manage.model.orderterm.OrderTermFloatingRateType;
import kg.gov.mf.loan.manage.model.orderterm.OrderTermFund;
import kg.gov.mf.loan.manage.service.collateral.ConditionTypeService;
import kg.gov.mf.loan.manage.service.collateral.InspectionResultTypeService;
import kg.gov.mf.loan.manage.service.collateral.ItemTypeService;
import kg.gov.mf.loan.manage.service.collateral.QuantityTypeService;
import kg.gov.mf.loan.manage.service.debtor.DebtorTypeService;
import kg.gov.mf.loan.manage.service.debtor.WorkSectorService;
import kg.gov.mf.loan.manage.service.loan.LoanStateService;
import kg.gov.mf.loan.manage.service.loan.LoanTypeService;
import kg.gov.mf.loan.manage.service.loan.PaymentTypeService;
import kg.gov.mf.loan.manage.service.order.CreditOrderService;
import kg.gov.mf.loan.manage.service.order.CreditOrderStateService;
import kg.gov.mf.loan.manage.service.order.CreditOrderTypeService;
import kg.gov.mf.loan.manage.service.orderterm.OrderTermCurrencyService;
import kg.gov.mf.loan.manage.service.orderterm.OrderTermDaysMethodService;
import kg.gov.mf.loan.manage.service.orderterm.OrderTermFloatingRateTypeService;
import kg.gov.mf.loan.manage.service.orderterm.OrderTermFundService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Transactional
@Component
public class Migration1Job implements Job{

    @Autowired
    CreditOrderService creditOrderService;


    @Autowired
    DebtorTypeService debtorTypeService;

    @Autowired
    LoanTypeService loanTypeService;

    @Autowired
    ItemTypeService itemTypeService;

    @Autowired
    QuantityTypeService quantityTypeService;

    @Autowired
    LoanStateService loanStateService;

    @Autowired
    WorkSectorService workSectorService;

    @Autowired
    PaymentTypeService paymentTypeService;

    @Autowired
    ConditionTypeService conditionTypeService;

    @Autowired
    InspectionResultTypeService inspectionResultTypeService;

    @Autowired
    OrderTermFloatingRateTypeService orderTermFloatingRateTypeService;

    @Autowired
    OrderTermDaysMethodService orderTermDaysMethodService;

    @Autowired
    OrderTermCurrencyService orderTermCurrencyService;

    @Autowired
    OrderTermFundService orderTermFundService;

    @Autowired
    CreditOrderStateService creditOrderStateService;

    @Autowired
    CreditOrderTypeService creditOrderTypeService;

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

        boolean orderStateMigrationDone = done;
        if(!orderStateMigrationDone) orderStateMigrationDone = this.orderStateMigrate(connection);

        boolean orderTypeMigrationDone = done;
        if(!orderTypeMigrationDone) orderTypeMigrationDone = this.orderTypeMigrate(connection);

        boolean loanFundMigrationDone = done;
        if(!loanFundMigrationDone) loanFundMigrationDone = this.loanFundMigrate(connection);

        boolean loanCurrencyMigrationDone = done;
        if(!loanCurrencyMigrationDone) loanCurrencyMigrationDone = this.loanCurrencyMigrate(connection);

        boolean daysCalcMethodMigrationDone = done;
        if(!daysCalcMethodMigrationDone) daysCalcMethodMigrationDone = this.daysCalcMethodMigrate(connection);

        boolean rateTypeMigrationDone = done;
        if(!rateTypeMigrationDone) rateTypeMigrationDone = this.rateTypeMigrate(connection);

        boolean debtorTypeMigrationDone = done;
        if(!debtorTypeMigrationDone) debtorTypeMigrationDone = this.debtorTypeMigrate(connection);

        boolean loanTypeMigrationDone = done;
        if(!loanTypeMigrationDone) loanTypeMigrationDone = this.loanTypeMigrate(connection);

        boolean itemTypeMigrationDone = done;
        if(!itemTypeMigrationDone) itemTypeMigrationDone = this.itemTypeMigrate(connection);

        boolean quantityTypeMigrationDone = done;
        if(!quantityTypeMigrationDone) quantityTypeMigrationDone = this.quantityTypeMigrate(connection);

        boolean paymentTypeMigrationDone = done;
        if(!paymentTypeMigrationDone) paymentTypeMigrationDone = this.paymentTypeMigrate(connection);

        boolean inspectionResultTypeMigrationDone = done;
        if(!inspectionResultTypeMigrationDone) inspectionResultTypeMigrationDone = this.inspectionResultTypeMigrate(connection);

        boolean loanStatusMigrationDone = done;
        if(!loanStatusMigrationDone) loanStatusMigrationDone = this.loanStatusMigrate(connection);

        boolean workSectorMigrationDone = done;
        if(!workSectorMigrationDone) workSectorMigrationDone = this.workSectorMigrate(connection);

        boolean creditOrderMigrationDone = done;
        if(!creditOrderMigrationDone) creditOrderMigrationDone = this.creditOrderMigrate(connection);
/*
        boolean rateTypeMigrationDone = false;
        if(!rateTypeMigrationDone) rateTypeMigrationDone = this.rateTypeMigrate(connection);
        */
        boolean debtorMigrationDone = false;
        if(!debtorMigrationDone) debtorMigrationDone = this.debtorMigrate(connection);




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


        System.out.println(" Debtor Type migration process == "+debtorTypeMigrationDone);
        System.out.println(" Loan Type migration process == "+loanTypeMigrationDone);
        System.out.println(" Item Type migration process == "+itemTypeMigrationDone);
        System.out.println(" Quantity Type migration process == "+quantityTypeMigrationDone);
        System.out.println(" Payment Type migration process == "+paymentTypeMigrationDone);
        System.out.println(" Inspection Result Type migration process == "+inspectionResultTypeMigrationDone);
        System.out.println(" Loan State migration process == "+loanStatusMigrationDone);
        System.out.println(" WorkSector migration process == "+workSectorMigrationDone);

        System.out.println(" Credit Order migration process == "+creditOrderMigrationDone);

    }


    private boolean debtorMigrate(Connection connection)
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
                            "  (select aokmotu.title from aokmotu where aokmotu.region = address.region_code and address.district_code = aokmotu.district and address.a_okmotu_code = aokmotu.aokmotu LIMIT 1 ) as aokmotu_title,\n" +
                            "  (select selo.title from selo where selo.region = address.region_code and address.district_code = selo.district and address.a_okmotu_code = selo.aokmotu and address.selo = selo.selo_code LIMIT 1 ) as selo_title,\n" +
                            "  (select aokmotu.id from aokmotu where aokmotu.region = address.region_code and address.district_code = aokmotu.district and address.a_okmotu_code = aokmotu.aokmotu LIMIT 1 ) as aokmotu_id,\n" +
                            "  (select selo.id from selo where selo.region = address.region_code and address.district_code = selo.district and address.a_okmotu_code = selo.aokmotu and address.selo = selo.selo_code LIMIT 1 ) as selo_id,\n" +
                            "\n" +
                            "*\n" +
                            "from person, person_details,address,phone\n" +
                            "where person.id = person_details.person_id AND\n" +
                            "      address.user_id = person.id AND address.contact_type = 2 AND\n" +
                            "      phone.user_id = person.id and phone.contact_type = 2 order by person.id  LIMIT  100 OFFSET 2000");
                    if(rs != null)
                    {
                        while (rs.next())
                        {

                            boolean isPerson = true;

                            Address address = new Address();

                            Region region = new Region();

                            if(this.regionService.findByCode(String.valueOf(rs.getInt("region")))!=null)
                                region = this.regionService.findByCode(String.valueOf(rs.getInt("region")));

                            address.setRegion(region);

                            District district = new District();

                            if(this.districtService.findById(rs.getInt("district")==0 ? 1 : rs.getInt("district"))!= null)
                                district = this.districtService.findById(rs.getInt("district")==0 ? 1 : rs.getInt("district"));

                            address.setDistrict(district);

                            Aokmotu aokmotu = new Aokmotu();
                            if(rs.getInt("aokmotu_id")>0)
                            {
                                aokmotu = this.aokmotuService.findById(rs.getInt("aokmotu_id"));
                                address.setAokmotu(aokmotu);
                            }


                            Village village = new Village();
                            if(rs.getInt("selo_id")>0)
                            {
                                village = this.villageService.findById(rs.getInt("selo_id"));
                                address.setVillage(village);
                            }

                            address.setLine(rs.getString("address_line1"));

                            AddressDetails addressDetails = new AddressDetails();

                            addressDetails.setName(String.valueOf(rs.getLong("person_id")));

//                            address.setAddressDetails(addressDetails);

                            Contact contact = new Contact();
                            if(!(rs.getString("number")=="" || rs.getString("number")==null))
                                contact.setName(rs.getString("number"));


                            IdentityDoc identityDoc = new IdentityDoc();

                            if(rs.getShort("type")==2) isPerson =false;

                            if(rs.getShort("document_type")==1)
                            {
                                identityDoc.setIdentityDocGivenBy(identityDocGivenByService.findById(1)); //MKK
                            }
                            else
                            {
                                identityDoc.setIdentityDocGivenBy(identityDocGivenByService.findById(2)); //MinJust
                                isPerson = false;
                            }

                            if(rs.getString("title").contains("ов")||
                                    rs.getString("title").contains("ова")||
                                    rs.getString("title").contains("ев")||
                                    rs.getString("title").contains("ева")||
                                    rs.getString("title").contains("уулу")||
                                    rs.getString("title").contains("кызы")
                                    )
                            {
                                if(!isPerson) isPerson = true;
                            }

                            identityDoc.setIdentityDocType(identityDocTypeService.findById(rs.getShort("document_type"))); // Passport


                            identityDoc.setEnabled(true);

                            if(rs.getDate("of_reg_date")!=null)
                                identityDoc.setDate(rs.getDate("of_reg_date"));

                            if(rs.getString("of_reg_id")!=null)
                            identityDoc.setPin(rs.getString("of_reg_id"));
                            else identityDoc.setPin("-");


                            identityDoc.setNumber(rs.getString("of_reg_series")+rs.getString("of_reg_number"));

                            identityDoc.setName(identityDoc.getIdentityDocType().getName() + " "+ identityDoc.getNumber());

                            IdentityDocDetails identityDocDetails = new IdentityDocDetails();
                            identityDocDetails.setFirstname(rs.getString("title"));
                            identityDocDetails.setLastname(rs.getString("title"));
                            identityDocDetails.setFullname(rs.getString("title"));
                            identityDocDetails.setMidname(rs.getString("title"));

                            identityDoc.setIdentityDocDetails(identityDocDetails);

                            if(isPerson)
                            {
                                Person person = new Person();
                                person.setName(rs.getString("title"));
                                person.setEnabled(true);
                                person.setAddress(address);
                                person.setContact(contact);
                                person.setIdentityDoc(identityDoc);
                                person.setDescription(String.valueOf(rs.getLong("person_id")));

                                this.personService.create(person);


                            }
                            else
                            {
                                Organization organization = new Organization();

                                organization.setName(rs.getString("title"));
                                organization.setAddress(address);
                                organization.setContact(contact);
                                organization.setIdentityDoc(identityDoc);
                                organization.setOrgForm(this.orgFormService.findById(2));
                                organization.setEnabled(true);
                                organization.setDescription(String.valueOf(rs.getLong("person_id")));

                                Department chief = new Department();
                                chief.setOrganization(organization);
                                chief.setName("Руководство");
                                chief.setDescription("");
                                chief.setEnabled(true);

                                Position responsible = new Position();
                                responsible.setName("Руководитель");

                                Position accountant = new Position();
                                accountant.setName("Гл. бухгалтер");

                                Set<Position> positions = new HashSet<Position>();
                                positions.add(responsible);
                                positions.add(accountant);

                                chief.setPosition(positions);

                                Set<Department> departments = new HashSet<Department>();
                                organization.setDepartment(departments);


                                this.organizationService.create(organization);


                                System.out.println(organization.getId());
                                /*Staff chiefStaff = new Staff();
                                chiefStaff.setOrganization(organization);
                                chiefStaff.setDepartment(chief);
                                chiefStaff.setPosition(responsible);
                                */

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



    private boolean creditOrderMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from credit_order ORDER BY id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            CreditOrder creditOrder = new CreditOrder();

                            if(rs.getString("number").length()>19)
                                creditOrder.setRegNumber(rs.getString("number").substring(0,19));
                            else creditOrder.setRegNumber(rs.getString("number"));

                            if(rs.getDate("date")!=null)
                                creditOrder.setRegDate(rs.getDate("date"));
                            else creditOrder.setRegDate(new Date());

                            CreditOrderType orderType = this.creditOrderTypeService.getById((long)rs.getInt("type"));
                            if(orderType!=null) creditOrder.setCreditOrderType(orderType);
                                else creditOrder.setCreditOrderType(this.creditOrderTypeService.getById((long)1));

                            CreditOrderState orderState = this.creditOrderStateService.getById((long)rs.getInt("record_status"));
                            if(orderState!=null) creditOrder.setCreditOrderState(orderState);

                            this.creditOrderService.add(creditOrder);

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




    private boolean workSectorMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 24 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            WorkSector newEntity = new WorkSector();
                            newEntity.setName(rs.getString("type_name"));

                            workSectorService.add(newEntity);
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


    private boolean loanStatusMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 23 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            LoanState newEntity = new LoanState();
                            newEntity.setName(rs.getString("type_name"));

                            loanStateService.add(newEntity);
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


    private boolean inspectionResultTypeMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 42 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            InspectionResultType newEntity = new InspectionResultType();
                            newEntity.setName(rs.getString("type_name"));

                            inspectionResultTypeService.add(newEntity);
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



    private boolean paymentTypeMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 41 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            PaymentType newEntity = new PaymentType();
                            newEntity.setName(rs.getString("type_name"));

                            paymentTypeService.add(newEntity);
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




    private boolean quantityTypeMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 22 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            QuantityType newEntity = new QuantityType();
                            newEntity.setName(rs.getString("type_name"));

                            quantityTypeService.add(newEntity);
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




    private boolean itemTypeMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 17 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            ItemType newEntity = new ItemType();
                            newEntity.setName(rs.getString("type_name"));

                            itemTypeService.add(newEntity);
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



    private boolean loanTypeMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 16 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            LoanType newEntity = new LoanType();
                            newEntity.setName(rs.getString("type_name"));

                            loanTypeService.add(newEntity);
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




    private boolean debtorTypeMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 15 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            DebtorType newEntity = new DebtorType();
                            newEntity.setName(rs.getString("type_name"));

                            debtorTypeService.add(newEntity);
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



    private boolean rateTypeMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 32 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            OrderTermFloatingRateType newEntity = new OrderTermFloatingRateType();


                            newEntity.setName(rs.getString("type_name"));

                            orderTermFloatingRateTypeService.add(newEntity);
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



    private boolean daysCalcMethodMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 31 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            OrderTermDaysMethod newEntity = new OrderTermDaysMethod();


                            newEntity.setName(rs.getString("type_name"));

                            orderTermDaysMethodService.add(newEntity);
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


    private boolean loanCurrencyMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 25 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            OrderTermCurrency newEntity = new OrderTermCurrency();


                            newEntity.setName(rs.getString("type_name"));

                            orderTermCurrencyService.add(newEntity);
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



    private boolean loanFundMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 47 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            OrderTermFund newEntity = new OrderTermFund();


                            newEntity.setName(rs.getString("type_name"));

                            orderTermFundService.add(newEntity);
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



    private boolean orderTypeMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 27 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            CreditOrderType newEntity = new CreditOrderType();

                            newEntity.setName(rs.getString("type_name"));

                            creditOrderTypeService.add(newEntity);
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


    private boolean orderStateMigrate(Connection connection)
    {
        boolean migrationSuccess = false;

        try
        {
            if (connection != null) {
                ResultSet rs = null;
                try
                {
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select * from system_type where system_type.group_id = 4 order by system_type.type_id");
                    if(rs != null)
                    {
                        while (rs.next())
                        {
                            CreditOrderState newEntity = new CreditOrderState();

                            newEntity.setName(rs.getString("type_name"));

                            creditOrderStateService.add(newEntity);
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

  /*          connection = DriverManager.getConnection(
                    "jdbc:postgresql://31.186.54.58:5432/migration2", "postgres",
                    "armad27raptor");

*/

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/migration", "postgres",
                    "armad27raptor");
        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return null;

        }

        return connection;
    }
}
