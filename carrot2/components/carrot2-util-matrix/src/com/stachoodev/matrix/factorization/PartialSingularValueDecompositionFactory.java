/*
 * NonnegativeMatrixFactorizationEDFactory.java Created on 2004-06-17
 */
package com.stachoodev.matrix.factorization;

import cern.colt.matrix.*;

/**
 * @author stachoo
 */
public class PartialSingularValueDecompositionFactory implements
        MatrixFactorizationFactory
{
    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.matrix.factorization.MatrixFactorizationFactory#factorize(cern.colt.matrix.DoubleMatrix2D)
     */
    public MatrixFactorization factorize(DoubleMatrix2D A)
    {
        return new PartialSingularValueDecomposition(A);
    }
}