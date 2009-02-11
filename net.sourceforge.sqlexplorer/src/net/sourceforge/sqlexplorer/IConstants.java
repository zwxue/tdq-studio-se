package net.sourceforge.sqlexplorer;

import org.eclipse.swt.graphics.Color;

import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;

/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

public interface IConstants {

	// The colour of borders and the fade-to-white colour of the selected tabs
	public static final Color TAB_BORDER_COLOR = new Color(null, 153, 186, 243);
	
	// Used for confirmations where the user can choose to always have the
	//	question answered in the same way in the future
	public enum Confirm {
		ASK,
		YES,
		NO
	}

    String AUTO_COMMIT = "SQLEditor.AutoCommit"; //$NON-NLS-1$

    String AUTO_OPEN_EDITOR = "SQLEditor.AutoOpenEditor"; //$NON-NLS-1$

    String CLIP_EXPORT_COLUMNS = "SQLEditor.ClipExportColumns"; //$NON-NLS-1$

    String CLIP_EXPORT_SEPARATOR = "SQLEditor.ClipExportSeparator"; //$NON-NLS-1$

    String COMMIT_ON_CLOSE = "SQLEditor.CommitOnClose"; //$NON-NLS-1$

    String DATASETRESULT_DATE_FORMAT = "DataSetResult.DateFormat"; //$NON-NLS-1$

    String DATASETRESULT_FORMAT_DATES = "DataSetResult.FormatDates"; //$NON-NLS-1$

    String DEFAULT_DRIVER = "Drivers.DefaultDriverName"; //$NON-NLS-1$

    String FONT = "SQLEditor.Font.V2"; //$NON-NLS-1$

    String HISTORY_AUTOSAVE_AFTER = "SQLHistory.AutoSaveAfterXXStatements"; //$NON-NLS-1$

    String INTERACTIVE_QUERY_TIMEOUT = "InteractiveConnection.QueryTimeOutSeconds"; //$NON-NLS-1$

    String MAX_SQL_ROWS = "SQLEditor.MaxSQLRows"; //$NON-NLS-1$

    String PRE_ROW_COUNT = "SQLEditor.PreRowCount"; //$NON-NLS-1$

    String SQL_ALT_QRY_DELIMITER = "SQLEditor.AltQueryDelimiter"; //$NON-NLS-1$

    String SQL_ASSIST = "SQLEditor.Assist"; //$NON-NLS-1$

    /** The color key for database tables column names */
    String SQL_COLUMS = "SQLEditor.ColumnsColor"; //$NON-NLS-1$
    
    String SQL_COMMENT_DELIMITER = "SQLEditor.CommentDelimiter"; //$NON-NLS-1$

    /**
     * The color key for everthing in SQL code for which no other color is
     * specified.
     */
    String SQL_DEFAULT = "SQLEditor.DefaultColor"; //$NON-NLS-1$

    String SQL_EDITOR_CLASS = SQLEditor.class.getName();

    /** The color key for SQL keywords in Java code. */
    String SQL_KEYWORD = "SQLEditor.KeywordColor"; //$NON-NLS-1$

    /** The color key for multi-line comments in Java code. */
    String SQL_MULTILINE_COMMENT = "SQLEditor.MultiLineCommentColor"; //$NON-NLS-1$

    String SQL_QRY_DELIMITER = "SQLEditor.QueryDelimiter"; //$NON-NLS-1$

    /** The color key for single-line comments in Java code. */
    String SQL_SINGLE_LINE_COMMENT = "SQLEditor.SingleLineCommentColor"; //$NON-NLS-1$

    /** The color key for string and character literals in Java code. */
    String SQL_STRING = "SQLEditor.StringColor"; //$NON-NLS-1$

    /** The color key for database tables names */
    String SQL_TABLE = "SQLEditor.TableColor"; //$NON-NLS-1$

    String WARN_LIMIT = "SQLEditor.WarnLimit"; //$NON-NLS-1$
    
    String WORD_WRAP = "SQLEditor.AutoWrap"; //$NON-NLS-1$
    
    // When executing the code in an editor, should we clear all the results tabs and 
    //	empty the messages list 
    String CLEAR_RESULTS_ON_EXECUTE = "SQLEditor.ClearResultsOnExecute"; //$NON-NLS-1$
    
    // Should the tabs just have a number (false), or should they have a snippit
    //	of the code (true).  Eg "q [select * from my_table...]"
    String USE_LONG_CAPTIONS_ON_RESULTS = "SQLEditor.UseLongCaptionsOnResults"; //$NON-NLS-1$
    
    // When executing more than one query from an editor, should the execution
    //	stop at the first error or carry on until the end logging all the errors
    //	in the message tab
    String STOP_ON_ERROR = "SQLEditor.StopOnError"; //$NON-NLS-1$
    
    String LOG_SUCCESS_MESSAGES = "SQLEditor.LogSuccess Messages"; //$NON-NLS-1$
    
    // Whether unsaved editors should prompt to be saved when they are closed 
    String REQUIRE_SAVE_ON_CLOSE_EDITOR = "SQLEditor.RequireSaveOnClose"; //$NON-NLS-1$
    
    // Whether Untitled editors should be saved to a scratch pad
    String SAVE_UNTITLED_IN_SCRATCH_PAD = "SQLEditor.SaveUntitledInScratchPad"; //$NON-NLS-1$
    
    // Whether structured comments are enabled in SQL queries
    String ENABLE_STRUCTURED_COMMENTS = "SQLEditor.EnableStructuredComments"; //$NON-NLS-1$
    
    // Debug logging level for queries
    String QUERY_DEBUG_LOG_LEVEL = "SQLEditor.QueryDebugLog"; //$NON-NLS-1$
    String QUERY_DEBUG_OFF = Messages.getString("IConstants.OFF"); //$NON-NLS-1$
    String QUERY_DEBUG_FAILED = Messages.getString("IConstants.Failed"); //$NON-NLS-1$
    String QUERY_DEBUG_ALL = Messages.getString("IConstants.All"); //$NON-NLS-1$
    
    // Yes/No/Ask confirmations
    String CONFIRM_YNA_SAVING_INSIDE_PROJECT = "Confirm.SavingOutsideProject"; //$NON-NLS-1$
    
    // Boolean confirmations
    String CONFIRM_BOOL_CLOSE_ALL_CONNECTIONS = "Confirm.CloseAllConnections"; //$NON-NLS-1$
    String CONFIRM_BOOL_CLOSE_CONNECTION = "Confirm.CloseConnection"; //$NON-NLS-1$
    String CONFIRM_BOOL_SHOW_DIALOG_ON_QUERY_ERROR = "Confirm.ShowDialogOnQueryError"; //$NON-NLS-1$
    String CONFIRM_BOOL_WARN_LARGE_MAXROWS = "Confirm.WarnIfLargeLimit"; //$NON-NLS-1$
}
