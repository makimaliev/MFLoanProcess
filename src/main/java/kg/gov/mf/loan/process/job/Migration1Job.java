package kg.gov.mf.loan.process.job;

import kg.gov.mf.loan.admin.org.model.*;
import kg.gov.mf.loan.admin.org.service.*;
import kg.gov.mf.loan.admin.sys.model.User;
import kg.gov.mf.loan.admin.sys.service.UserService;
import kg.gov.mf.loan.manage.model.collateral.InspectionResultType;
import kg.gov.mf.loan.manage.model.collateral.ItemType;
import kg.gov.mf.loan.manage.model.collateral.QuantityType;
import kg.gov.mf.loan.manage.model.debtor.*;
import kg.gov.mf.loan.manage.model.loan.*;
import kg.gov.mf.loan.manage.model.order.CreditOrder;
import kg.gov.mf.loan.manage.model.order.CreditOrderState;
import kg.gov.mf.loan.manage.model.order.CreditOrderType;
import kg.gov.mf.loan.manage.model.orderterm.*;
import kg.gov.mf.loan.manage.service.collateral.ConditionTypeService;
import kg.gov.mf.loan.manage.service.collateral.InspectionResultTypeService;
import kg.gov.mf.loan.manage.service.collateral.ItemTypeService;
import kg.gov.mf.loan.manage.service.collateral.QuantityTypeService;
import kg.gov.mf.loan.manage.service.debtor.DebtorService;
import kg.gov.mf.loan.manage.service.debtor.DebtorTypeService;
import kg.gov.mf.loan.manage.service.debtor.OrganizationFormService;
import kg.gov.mf.loan.manage.service.debtor.WorkSectorService;
import kg.gov.mf.loan.manage.service.loan.*;
import kg.gov.mf.loan.manage.service.order.CreditOrderService;
import kg.gov.mf.loan.manage.service.order.CreditOrderStateService;
import kg.gov.mf.loan.manage.service.order.CreditOrderTypeService;
import kg.gov.mf.loan.manage.service.orderterm.*;
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
    CreditTermService creditTermService;

    @Autowired
    OrderTermTransactionOrderService orderTermTransactionOrderService;

    @Autowired
    OrderTermRatePeriodService orderTermRatePeriodService;

    @Autowired
    PaymentScheduleService paymentScheduleService;

    @Autowired
    InstallmentStateService installmentStateService;

    @Autowired
    LoanService loanService;


    @Autowired
    DebtorService debtorService;


    @Autowired
    CreditOrderService creditOrderService;

    @Autowired
    OrganizationFormService organizationFormService;


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

    Set<String> errorList = new HashSet<String>();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        Connection connection = this.getSourceConnection();



        boolean done = false;

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

        boolean debtorMigrationDone = done;
        if(!debtorMigrationDone) debtorMigrationDone = this.debtorMigrate(connection);


        /*
        boolean rateTypeMigrationDone = false;
        if(!rateTypeMigrationDone) rateTypeMigrationDone = this.rateTypeMigrate(connection);
        */




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

        for (String text: errorList)
        {
            System.out.println(text);
        }

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
                    // sql code get person data
                    Statement st = connection.createStatement();
                    rs = st.executeQuery("select\n" +
                            " aokmotu.id  as aokmotu_id,\n" +
                            "  selo.id  as selo_id,\n" +
                            " \n " +
                            " *\n " +
                            " from person, person_details,address,phone,aokmotu,selo \n " +
                            " where person.id = person_details.person_id AND aokmotu.region = address.region_code and address.district_code = aokmotu.district and address.a_okmotu_code = aokmotu.aokmotu AND " +
                            " selo.region = address.region_code and address.district_code = selo.district and address.a_okmotu_code = selo.aokmotu and address.selo = selo.selo_code AND \n" +
                            "      address.user_id = person.id AND address.contact_type = 2 AND\n" +
                            "      phone.user_id = person.id and phone.contact_type = 2 order by person.id  LIMIT  50 OFFSET 300");
                    if(rs != null)
                    {
                        while (rs.next())
                        {

                            boolean isPerson = true;

                            //address
                            Address address = new Address();

                            Region region = new Region();

                            if(rs.getInt("region")==0)
                                errorList.add(" debtor region error "+rs.getInt("person_id"));
                            else
                                region = this.regionService.findByCode(String.valueOf(rs.getInt("region")));

                            address.setRegion(region);

                            District district = new District();

                            if(rs.getInt("district")==0)
                                errorList.add(" debtor district error "+rs.getInt("person_id"));
                            else
                                district = this.districtService.findById(rs.getInt("district")==0 ? 1 : rs.getInt("district"));

                            address.setDistrict(district);

                            Aokmotu aokmotu = new Aokmotu();
                            if(rs.getInt("aokmotu_id")>0)
                            {
                                aokmotu = this.aokmotuService.findById(rs.getInt("aokmotu_id"));
                                address.setAokmotu(aokmotu);
                            }
                            else
                            {
                                aokmotu = this.aokmotuService.findById(1);
                                address.setAokmotu(aokmotu);
                            }


                            Village village = new Village();
                            if(rs.getInt("selo_id")>0)
                            {
                                village = this.villageService.findById(rs.getInt("selo_id"));
                                address.setVillage(village);
                            }
                            else
                            {
                                village = this.villageService.findById(1);
                                address.setVillage(village);
                            }

                            address.setLine(rs.getString("address_line1"));

//                            AddressDetails addressDetails = new AddressDetails();

  //                          addressDetails.setName(String.valueOf(rs.getLong("person_id")));

//                            address.setAddressDetails(addressDetails);

                            //contact
                            Contact contact = new Contact();

                            if(!(rs.getString("number")=="" || rs.getString("number")==null))
                                contact.setName(rs.getString("number"));

                            if(!(rs.getString("mobile")=="" || rs.getString("mobile")==null))
                                contact.setName(contact.getName()+", "+rs.getString("mobile"));

                            // id doc
                            IdentityDoc identityDoc = new IdentityDoc();

                            if(rs.getShort("type")==2) isPerson =false; // isOrganization

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
                                if(!isPerson && rs.getShort("document_type")!=1) isPerson = false;
                            }

                            identityDoc.setIdentityDocType(identityDocTypeService.findById(rs.getShort("document_type"))); // Passport


                            identityDoc.setEnabled(true);

                            if(rs.getDate("of_reg_date")!=null)
                                identityDoc.setDate(rs.getDate("of_reg_date"));
                            else identityDoc.setDate(new Date());

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

                            // person and organization

                            Person person = new Person();
                            Organization organization = new Organization();

                            if(isPerson)
                            {

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
                            // owner
                            Owner owner = new Owner();
                            if(isPerson)
                            {
                                owner.setName(person.getName());
                                owner.setOwnerType(OwnerType.PERSON);
                                owner.setEntityId(person.getId());
                            }
                            else
                            {
                                owner.setName(organization.getName());
                                owner.setOwnerType(OwnerType.ORGANIZATION);
                                owner.setEntityId(organization.getId());
                            }

                            Debtor debtor = new Debtor();

                            debtor.setName(owner.getName());
                            debtor.setOwner(owner);
                            debtor.setDebtorType(this.debtorTypeService.getById((long)1));
                            debtor.setOrgForm(this.organizationFormService.getById((long)1));
                            debtor.setWorkSector(this.workSectorService.getById((long)rs.getInt("work_sector")));

                            this.debtorService.add(debtor);

                            try
                            {
                                if (connection != null) {
                                    ResultSet rsLoan = null;
                                    try
                                    {
                                        Statement stLoan = connection.createStatement();
                                        rsLoan = stLoan.executeQuery("select * from credit,credit_details where credit.id = credit_details.credit_id and credit.person_id = "+rs.getInt("person_id"));
                                        if(rsLoan != null)
                                        {
                                            while (rsLoan.next())
                                            {

                                                Loan loan = new Loan();
                                                loan.setAmount(rsLoan.getDouble("cost"));
                                                loan.setCreditOrder(this.creditOrderService.getById((long)rsLoan.getInt("credit_order_id")));
                                                loan.setSupervisorId(rsLoan.getLong("curator"));
                                                loan.setLoanType(this.loanTypeService.getById((long)rsLoan.getInt("credit_type")));
                                                loan.setCurrency(this.orderTermCurrencyService.getById((long)rsLoan.getInt("currency")));
                                                loan.setRegDate(rsLoan.getDate("date"));
                                                loan.setRegNumber(rsLoan.getString("number"));
                                                loan.setDebtor(debtor);
                                                loan.setLoanState(this.loanStateService.getById((long)rsLoan.getInt("status")));


                                                this.loanService.add(loan);


                                                Boolean penalyLimit20 = true;

                                                // schedule migration
                                                try
                                                {
                                                    if (connection != null) {
                                                        ResultSet rsSchedule = null;
                                                        try
                                                        {
                                                            Statement stSchedule = connection.createStatement();
                                                            rsSchedule = stSchedule.executeQuery("select * from obligation where obligation.credit_id = "+rsLoan.getInt("credit_id"));
                                                            if(rsSchedule != null)
                                                            {
                                                                while (rsSchedule.next())
                                                                {
                                                                    PaymentSchedule paymentSchedule = new PaymentSchedule();
                                                                    paymentSchedule.setLoan(loan);
                                                                    paymentSchedule.setDisbursement(rsSchedule.getDouble("profit"));
                                                                    paymentSchedule.setPrincipalPayment(rsSchedule.getDouble("debt_payment"));
                                                                    paymentSchedule.setInterestPayment(rsSchedule.getDouble("debt_percent"));
                                                                    paymentSchedule.setCollectedInterestPayment(rsSchedule.getDouble("collected_debt_percent"));
                                                                    paymentSchedule.setCollectedPenaltyPayment(rsSchedule.getDouble("collected_debt_penalty"));
                                                                    paymentSchedule.setExpectedDate(rsSchedule.getDate("date"));
                                                                    paymentSchedule.setInstallmentState(this.installmentStateService.getById((long)1));

                                                                    this.paymentScheduleService.add(paymentSchedule);

                                                                }

                                                                migrationSuccess = true;
                                                                stSchedule.close();
                                                                rsSchedule.close();
                                                            }
                                                        }
                                                        catch (SQLException ex)
                                                        {
                                                            System.out.println("Connection Failed! Check output console");
                                                            ex.printStackTrace();
                                                            errorList.add(" scehdule error" + ex);
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
                                                    errorList.add(" scehdule connection error" + ex);
                                                }

                                                // credit term migration

                                                try
                                                {
                                                    if (connection != null) {
                                                        ResultSet rsTerm = null;
                                                        try
                                                        {
                                                            Statement stTerm = connection.createStatement();
                                                            rsTerm = stTerm.executeQuery("select * from credit_rule where credit_rule.credit_id = "+rsLoan.getInt("credit_id"));
                                                            if(rsTerm != null)
                                                            {
                                                                while (rsTerm.next())
                                                                {
                                                                    CreditTerm term = new CreditTerm();

                                                                    term.setLoan(loan);

                                                                    term.setStartDate(rsTerm.getDate("start_date"));

                                                                    term.setInterestRateValue(rsTerm.getDouble("percent_rate"));

                                                                    term.setPenaltyOnPrincipleOverdueRateValue(rsTerm.getDouble("penalty_main_debt"));

                                                                    term.setPenaltyOnInterestOverdueRateValue(rsTerm.getDouble("penalty_percent"));

                                                                    if(rsTerm.getInt("method_days_period")<3)
                                                                        term.setDaysInMonthMethod((OrderTermDaysMethod)this.orderTermDaysMethodService.getById((long)rsTerm.getInt("method_days_period")));
                                                                    else
                                                                        {
                                                                            term.setDaysInMonthMethod((OrderTermDaysMethod)this.orderTermDaysMethodService.getById((long)2));
                                                                            errorList.add(" credit term error 1 "+loan.getId());
                                                                        }

                                                                     if(rsTerm.getInt("method_days")<3)
                                                                        term.setDaysInYearMethod((OrderTermDaysMethod)this.orderTermDaysMethodService.getById((long)rsTerm.getInt("method_days")));
                                                                    else
                                                                     {
                                                                         term.setDaysInYearMethod((OrderTermDaysMethod)this.orderTermDaysMethodService.getById((long)2));
                                                                         errorList.add(" credit term error 2 "+loan.getId()+" "+rsTerm.getInt("method_days"));
                                                                     }

                                                                     if(rsTerm.getInt("rate_type")>0)
                                                                        term.setFloatingRateType((OrderTermFloatingRateType)this.orderTermFloatingRateTypeService.getById((long)rsTerm.getInt("rate_type")));

                                                                    if(rsTerm.getInt("plus_penalty")>0)
                                                                        term.setPenaltyOnPrincipleOverdueRateType((OrderTermFloatingRateType)this.orderTermFloatingRateTypeService.getById((long)rsTerm.getInt("plus_penalty")));

                                                                    if(rsTerm.getInt("plus_penalty")>0)
                                                                        term.setPenaltyOnInterestOverdueRateType((OrderTermFloatingRateType)this.orderTermFloatingRateTypeService.getById((long)rsTerm.getInt("plus_percent")));

                                                                    if(rsTerm.getInt("repayment_main_debt")==1)
                                                                        term.setTransactionOrder((OrderTermTransactionOrder)this.orderTermTransactionOrderService.getById((long)1));
                                                                    else term.setTransactionOrder((OrderTermTransactionOrder)this.orderTermTransactionOrderService.getById((long)3));

                                                                    term.setRatePeriod(this.orderTermRatePeriodService.getById((long)1));

                                                                    if(penalyLimit20)
                                                                    term.setPenaltyLimitPercent((double)20);
                                                                    else term.setPenaltyLimitPercent((double)0);

                                                                    this.creditTermService.add(term);

                                                                }

                                                                migrationSuccess = true;
                                                                stTerm.close();
                                                                rsTerm.close();
                                                            }
                                                        }
                                                        catch (SQLException ex)
                                                        {
                                                            System.out.println("Connection Failed! Check output console");
                                                            ex.printStackTrace();
                                                            errorList.add(" credit term error 0" + ex);
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
                                                    errorList.add(" credit term connection error" + ex);
                                                }


                                            }

                                            migrationSuccess = true;
                                            rsLoan.close();
                                            stLoan.close();
                                        }
                                    }
                                    catch (SQLException ex)
                                    {
                                        System.out.println("Connection Failed! Check output console");
                                        ex.printStackTrace();
                                        errorList.add("loan error"+ex);
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
                    errorList.add("debtor migration error: query ="+ex);
                    return migrationSuccess;
                }

            }
            else
            {
                System.out.println("Failed to make connection!");
                errorList.add("debtor migration error: connection");
            }
        }
        catch(Exception ex)
        {
            System.out.println(" Error in User migration "+ex);
            errorList.add("debtor migration error: connection"+ex);
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

                    errorList.add("region migration error: query"+ex);
                    return migrationSuccess;
                }

            }
            else
            {
                System.out.println("Failed to make connection!");
                errorList.add("region migration error: connection");
            }
        }
        catch(Exception ex)
        {
            System.out.println(" Error in User migration "+ex);
            errorList.add("region migration error: connection"+ex);
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
                    errorList.add("orgFrom migration error: query error"+ex);
                    return migrationSuccess;
                }

            }
            else
            {
                System.out.println("Failed to make connection!");
                errorList.add("orgFrom migration error: no connection");
            }
        }
        catch(Exception ex)
        {
            System.out.println(" Error in OrgForm migration "+ex);
            errorList.add("orgFrom migration error"+ex);
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

            errorList.add(" Class for name error"+e);
            return null;

        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        try {

  /*          connection = DriverManager.getConnection(
                    "jdbc:postgresql://31.186.54.58:5432/migration2", "postgres",
                    "armad27raptor");

*/

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://150.0.0.4:5432/migration2", "postgres",
                    "armad27raptor");
        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();

            errorList.add("connection error"+e);
            return null;

        }

        return connection;
    }
}
