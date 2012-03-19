/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package reg.spreadsheet;

import java.awt.Color;

/**
 *
 * @author robertriedel
 */
public class SpreadsheetConstants {

    /* DAT LOAD TOKENIZER STRINGS */

    public final static String LOAD_DATA = "#data";
    public final static String LOAD_HEADERS = "#headers";
    public final static String LOAD_END = "#end";

    /* DIALOG INFO MESSAGES */

    public final static String INFO_MSG_SAVETEMP = java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("PLEASE_CHOOSE_A_TEMPLATE_FILENAME");
    public final static String INFO_MSG_SAVEDAT =  java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("PLEASE_CHOOSE_A_FILENAME");

    /* DIALOG TITLES */

    public final static String DLG_TITLE_CUSTOMIZE = java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("CUSTOMIZE_RENDERER");
    public final static String DLG_TITLE_JTSCONFIGURATOR ="JTS Viewer";
    public final static String DLG_TITLE_JXYSCONFIGURATOR ="XYPlot Viewer";

    /* DIALOG ERROR MESSAGES */

    public final static String STP_ERR_NOTEMPFOUND = java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("NO_TEMPLATE_FILES_FOUND_IN_THE_WORKSPACE_DIRECTORY!_") +
                    java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("USE_THE_'SAVE_TEMPLATE'_OPTION_IN_THE_TIME_PLOT_CONFIGURATOR!");
    public final static String SPREADSHEET_ERR_TSMISSING = java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("TIME_SERIES_MISSING!");

    public final static String JXY_ERR_NODATATEMPLATE = java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("NO_TEMPLATE_FOR_DATAPLOT._USE_AT_LEAST_2_COLUMNS");

    /* GUI ELEMENT COLORS */

//    public final static Color GUI_COLOR_CLOSETAB = Color.DARK_GRAY;

    /* FILE NAMES */
    public final static String FILE_ENDING_TTP = ".ttp";
    public final static String FILE_ENDING_DAT = ".sdat";
    public final static String FILE_EXPLORER_DIR_NAME = "/explorer";

    /* STP TITLE */
    public final static String STP_TITLE = "StackedTimePlot Configurator";

    /* FONTS */

    /* STRINGS */


    /* DEFAULT ST PLOT PROPERTIES */

    /* DEFAULT JTS PLOT PROPERTIES */

    public final static int JTS_DEFAULT_STROKE = 1;
    public final static int JTS_DEFAULT_SHAPE_SIZE = 3;
    public final static int JTS_DEFAULT_SHAPE = 1;

    /* DEFAULT JXYS PLOT PROPERTIES */

    public final static int JXYS_DEFAULT_STROKE = 0;
    public final static int JXYS_DEFAULT_SHAPE_SIZE = 1;
    public final static int JXYS_DEFAULT_SHAPE = 5;
}
