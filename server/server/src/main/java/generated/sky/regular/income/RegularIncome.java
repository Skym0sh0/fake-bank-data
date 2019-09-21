/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income;


import generated.sky.regular.income.tables.BankStatement;
import generated.sky.regular.income.tables.FinancialTransaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.12"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RegularIncome extends SchemaImpl {

    private static final long serialVersionUID = -829517180;

    /**
     * The reference instance of <code>REGULAR_INCOME</code>
     */
    public static final RegularIncome REGULAR_INCOME = new RegularIncome();

    /**
     * The table <code>REGULAR_INCOME.bank_statement</code>.
     */
    public final BankStatement BANK_STATEMENT = generated.sky.regular.income.tables.BankStatement.BANK_STATEMENT;

    /**
     * The table <code>REGULAR_INCOME.financial_transaction</code>.
     */
    public final FinancialTransaction FINANCIAL_TRANSACTION = generated.sky.regular.income.tables.FinancialTransaction.FINANCIAL_TRANSACTION;

    /**
     * No further instances allowed
     */
    private RegularIncome() {
        super("REGULAR_INCOME", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            BankStatement.BANK_STATEMENT,
            FinancialTransaction.FINANCIAL_TRANSACTION);
    }
}
