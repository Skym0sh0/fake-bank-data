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
public class UuidGenerateV1 extends AbstractRoutine<UUID> {

    private static final long serialVersionUID = -1920456983;

    /**
     * The parameter <code>REGULAR_INCOME.uuid_generate_v1.RETURN_VALUE</code>.
     */
    public static final Parameter<UUID> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.SQLDataType.UUID, false, false);

    /**
     * Create a new routine call instance
     */
    public UuidGenerateV1() {
        super("uuid_generate_v1", RegularIncome.REGULAR_INCOME, org.jooq.impl.SQLDataType.UUID);

        setReturnParameter(RETURN_VALUE);
    }
}
