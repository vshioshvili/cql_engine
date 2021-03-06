package org.opencds.cqf.cql.elm.execution;

import org.opencds.cqf.cql.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.execution.Context;
import org.opencds.cqf.cql.runtime.Quantity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/*
Variance(argument List<Decimal>) Decimal
Variance(argument List<Quantity>) Quantity

The Variance operator returns the statistical variance of the elements in source.
If the source contains no non-null elements, null is returned.
If the source is null, the result is null.
Return types: BigDecimal & Quantity
*/

public class VarianceEvaluator extends org.cqframework.cql.elm.execution.Variance {

    public static Object variance(Object source) {

        if (source == null) {
            return null;
        }

        if (source instanceof Iterable) {

            if (((List) source).isEmpty()) {
                return null;
            }

            Object mean = AvgEvaluator.avg(source);

            List<Object> newVals = new ArrayList<>();

            for (Object element : (Iterable) source) {
                if (element != null) {
                    if (element instanceof BigDecimal || element instanceof Quantity) {
                        newVals.add(MultiplyEvaluator.multiply(
                                SubtractEvaluator.subtract(element, mean),
                                SubtractEvaluator.subtract(element, mean))
                        );
                    }
                    else {
                        throw new InvalidOperatorArgument(
                                "Variance(List<Decimal>) or Variance(List<Quantity>)",
                                String.format("Variance(List<%s>)", element.getClass().getName())
                        );
                    }
                }
            }

            return DivideEvaluator.divide(SumEvaluator.sum(newVals), new BigDecimal(newVals.size() - 1)); // slight variation to Avg
        }

        throw new InvalidOperatorArgument(
                "Variance(List<Decimal>) or Variance(List<Quantity>)",
                String.format("Variance(%s)", source.getClass().getName()));
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object source = getSource().evaluate(context);
        return variance(source);
    }
}
