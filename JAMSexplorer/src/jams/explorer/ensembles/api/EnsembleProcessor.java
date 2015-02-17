/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jams.explorer.ensembles.api;

import jams.data.Attribute;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;

/**
 *
 * @author christian
 * @param <U>
 * @param <T>
 */
public interface EnsembleProcessor<U extends Model, T extends Ensemble<U>> {        
    public int getModelCount();
        
    public Attribute.Calendar[] getTimeDomain() throws Exception;
    public long[] getEntityIDs() throws Exception;
    public double[][] getSpatialMean() throws Exception;
    public double[] getSpatialMean(U model) throws Exception;
}