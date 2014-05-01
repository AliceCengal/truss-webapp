package edu.vanderbilt.truss.engine;

import edu.vanderbilt.truss.InputStruct;
import edu.vanderbilt.truss.reporter.ResultSetReporter;

/**
 * Does the computation
 *
 * Created by athran on 4/17/14.
 */
public interface TrussEngine {

    void injectInputData(InputStruct input);
    void injectOutput(ResultSetReporter reporter);
    void compute();

}
