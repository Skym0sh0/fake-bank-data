/*
 * This file is generated by jOOQ.
 */
package generated.sky.regular.income.routines;


import generated.sky.regular.income.RegularIncome;

import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.Internal;


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
public class UuidNil extends AbstractRoutine<UUID> {

    private static final long serialVersionUID = 631894151;

    /**
     * The parameter <code>REGULAR_INCOME.uuid_nil.RETURN_VALUE</code>.
     */
    public static final Parameter<UUID> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.SQLDataType.UUID, false, false);

    /**
     * Create a new routine call instance
     */
    public UuidNil() {
        super("uuid_nil", RegularIncome.REGULAR_INCOME, org.jooq.impl.SQLDataType.UUID);

        setReturnParameter(RETURN_VALUE);
    }
}
