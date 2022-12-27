/*
 * Copyright (c) 2020-2022 Vikash Madhow
 */

package ma.vi.esql.builder;

/**
 * A standard set metadata of attributes which can be attached to tables and fields
 * through their metadata definition. Attributes are interpreted on both the
 * database and the client. For interpretation on the client they will be sent as
 * an uncomputed expression in the form:
 *
 * <pre>${attribute uncomputed expression}</pre>
 *
 * To assist with interpretation of the expression on the client, all column
 * references are prefixed with `row.`. E.g.:
 *
 * <code>x + y</code> is sent to the client as <code>${row.x + row.y}</code>
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Attributes {

  /**
   * If set in the metadata of the id of a record, indicates that the
   * record is new and should be inserted in the database. The absence
   * of this attribute indicates that a record  with the same id should
   * already exist in the database.
   */
  public static final String NEW_RECORD = "new_record";

  /*
   * If set in the metadata of a children field (children are the one-to-many
   * associated records linked to the current record), contains an array of ids
   * of children records which should be deleted from the server.
   */
  public static final String DELETED_CHILDREN = "deleted_children";

  /**
   * Added to the metadata of records and columns when modified.
   */
  public static final String CHANGED = "$changed";

  /*
   * An initial value to set on a field on creation.
   */
  public static final String INITIAL_VALUE = "initial_value";

  /**
   * Name of the default ID column.
   */
  public static final String ID = "_id";

  /**
   * The data type of the column on which this attribute is set.
   */
  public static final String TYPE = "_type";

  /**
   * The expression to compute as the default value for a column when no value is
   * specified in an insert statement, and the expression to compute the value for
   * a derived column.
   */
  public static final String EXPRESSION = "_expression";

  /**
   * The name of a column containing a sequence number to order a set
   * rows by.
   */
  public static final String SEQUENCE_COLUMN = "sequence_column";

  /**
   * The name a column that will contain the id of a parent row.
   */
  public static final String PARENT_LINK_COLUMN = "link_column";

  /**
   * Set to true on derived fields.
   */
  public static final String DERIVED = "derived";

  /**
   * The expression for computing value of non-derived fields when they are being
   * edited. This expression should use conditionals to not overwrite manual changes
   * made by the user to the field.
   */
  public static final String VALUE_EXPRESSION = "value_expression";

  /**
   * A human-readable title for the member. The label may contain member references
   * in the form of {@code ${<property_name>}} where property_name is the name of a property
   * relative to the object where this member is present. Member references are
   * replaced with their actual value before display at runtime.
   * <p>
   * When using member references in label, shortLabel should be supplied explicitly
   * as, otherwise, it will be set to the label value. Since short labels are used as
   * relation headers where there is no context for resolving the references, the member
   * reference will be shown without any resolution in the column header.
   */
  public static final String LABEL = "label";

  /**
   * A shorter title useful in situations where the real title might
   * be too long such as for the heading of column in a relation.
   */
  public static final String SHORT_LABEL = "short_label";

  // @todo table level attributes: candidate keys, table level validations and warnings

  /**
   * A human readable name of the entity.
   */
  public static final String NAME = "name";

  /**
   * A hint to be displayed with this member such as in a tooltip. The hint
   * can a string containing member references in the form of {@code ${<property_name>}} where
   * property_name is the name of a property relative to the object where this
   * member is present. Member references are replaced with their actual value at
   * runtime.
   *
   * A description of table and fields, primarily for documentary purposes.
   */
  public static final String DESCRIPTION = "description";

  /**
   * Set on a table that is opting into the history tracking system. When this
   * is set to true, a history table to capture change events for the table is
   * created and a trigger is set on the table to capture change events.
   */
  public static final String HISTORY = "history";

  /**
   * An expression to compute a string form for a table record. This
   * form is used in place of the full record in places where the latter
   * would not fit, such as inside a drop-drown menu. When not specified
   * the value of the first column marked as the main display is used to
   * compute the string form. If no column has been marked as such, the
   * first non-id column is used. If there is no other column than ID,
   * the latter is used. Otherwise the string-form is null.
   */
  public static final String STRING_FORM = "string_form";

  /**
   * If computes to true, the field cannot be changed. Default is false.
   */
  public static final String READONLY = "readonly";

  /**
   * If true, column is not shown. Default is false.
   */
  public static final String HIDE = "hide";

  /**
   * If true, column is not shown when editing. Default is false.
   */
  public static final String EDIT_HIDE = "edit_hide";

  /**
   * If true, column is not shown when browsing. Default is false.
   */
  public static final String BROWSE_HIDE = "browse_hide";

  // File upload and download
  ///////////////////////////////////////////////////////
  /**
   * If set to true, files can be uploaded into this column as
   * raw binary data and stored as an external file (outside the
   * database). The file can be downloaded through a provided link.
   */
  public static final String EXTERNAL_FILE = "external_file";

  /**
   * The type of files that can be stored in the column. The column must be of
   * type text. Files are stored as a JSON object with the following structure:
   * {
   *   name: `filename`,
   *   size: `size of file in bytes`,
   *   type: `mime type of file`,
   *   data: `base64 encoded content of file`
   * }
   */
  public static final String FILE_TYPE = "file_type";

  /**
   * The order in which this member should appear when the object is being
   * edited/viewed. Fields with lower sequence numbers are displayed before
   * those with higher ones. Fields without a sequence number will be given
   * one based on the order that they were added to the table. New fields
   * added through ALTER TABLE are always added at the end of the table.
   */
  public static final String SEQUENCE = "sequence";

  /**
   * If records can be sorted by this column. Default is true.
   */
  public static final String SORTABLE = "sortable";

  /**
   * Sort order for the column (asc, desc or null (default)).
   */
  public static final String SORT = "sort";

  /**
   * Whether this column could be exported. Default is true.
   */
  public static final String EXPORTABLE = "exportable";

  /**
   * Indicate that some processing is ongoing for the column.
   */
  public static final String LOADING = "loading";

  /**
   * A main display column is used primarily when all members cannot
   * be displayed (e.g. when the object is occupying a cell in a bigger
   * table and is constrained by the width of the cell). It could also
   * used as the default filter member or shown with added emphasis.
   */
  public static final String MAIN_COLUMN = "main_column";

  /**
   * Marks a column as containing a unique readable identifier for the record.
   * For example, an employee number, a company code. etc.
   */
  public static final String IDENTIFIER = "identifier";

  /**
   * A formatting pattern to apply to the value of the member when latter need to
   * be converted to a string or back. The formatting is done on the client-side.
   */
  public static final String FORMAT = "format";

  /**
   * Prevents formatting of the column value. Useful for disabling default formatting
   * such as number groupings in years.
   */
  public static final String NO_FORMAT = "no_format";

  /**
   * Format inputs with digits groupings and 2 decimal places.
   */
  public static final String MONEY = "money";

  /**
   * Text to display when something (a switch, e.g.) is active.
   */
  public static final String ACTIVE_TEXT = "active_text";

  /**
   * Text to display when something (a switch, e.g.) is inactive.
   */
  public static final String INACTIVE_TEXT = "inactive_text";


  /**
   * Date resolution: whether to select date (i.e day, default), week, month or year.
   */
  public static final String DATE_RESOLUTION = "date_resolution";


  // Grid spacing
  /////////////////////////////////////////

  /*
   * The number of grid cells to span for this field
   */
  public static final String SPAN = "span";

  /*
   * How may grid cells to offset field by. Moves the field by that many cell to the right.
   */
  public static final String OFFSET = "offset";

//    /**
//     * When editing this column, lay out the corresponding component on a new line
//     */
//    public static final String NEW_LINE = "new_line";

  /**
   * Returns information on how to group this column with other
   * columns, if applicable.
   * <p>
   * Groups columns by a common name.
   * <p>
   * Groups with the same name can be
   * shown together as tabs, panes, etc. The group name may also contain a
   * colon followed by a suffix which provides additional information on
   * how to display the group data. For example ':tabular' will display
   * the group columns as a table.
   */
  public static final String GROUP = "group";

  /*
   * Second grouping level: allows fields to be grouped into a less conspicuous
   * grouping such as a box inside the same tab as the remaining fields.
   */
  public static final String SUBGROUP = "subgroup";

  /**
   * When this is set to true the value is hidden and only edited through a
   * password field.
   */
  public static final String PASSWORD = "password";

  /**
   * Columns where this is &gt; 1 will be edited through a multi-line text
   * field instead of a normal text field. The target field must be able
   * to receive string data. The multiline text field will contain as many
   * lines as the value of this attribute.
   */
  public static final String LINES = "lines";

  /**
   * Maximum number of lines that a text area will be allowed to grow to.
   */
  public static final String MAX_LINES = "max_lines";

  /**
   * The language of the content of the field. Can be Javascript, C, etc.
   * If this is set, a special editor able to edit the language is used
   * instead of the plain texarea.
   */
  public static final String CODE_LANGUAGE = "code_language";

  /**
   * When set on a member, signifies that the member can hold multiple values
   * of a lookup or entity. The values are stored in the member, which must be
   * of type string, as a comma-separated list of ids or codes. For lookups,
   * the display.lookup member must also be set, while for entities, the candidateClass
   * must be set to the target entity class.
   */
  public static final String MULTI_SELECT = "multi_select";

  /**
   * When set, a special markdown editor is shown instead of the normal
   * text-area editor.
   */
  public static final String MARKDOWN = "markdown";

// Excel report formatting
////////////////////////////////////////////////////////

// fonts
// export const FONT_NAME      = "font_name";
// export const TEXT_COLOR     = "text_color";
// export const BOLD           = "bold";
// export const ITALIC         = "italic";
// export const UNDERLINE      = "underline";
// export const STRIKETHROUGH  = "strikethrough";
// export const FONT_SIZE      = "font_size";
//
// // alignment
// export const ALIGN_LEFT     = "align_left";
// export const ALIGN_RIGHT    = "align_right";
// export const ALIGN_CENTER   = "align_center";
// export const ALIGN_JUSTIFY  = "align_justify";
//
// // borders
// export const BORDER_LEFT     = "border_left";
// export const BORDER_RIGHT    = "border_right";
// export const BORDER_TOP      = "border_top";
// export const BORDER_BOTTOM   = "border_bottom";
// export const BORDER_DIAGONAL = "border_diagonal";
//
//  public static final String EXCEL_FONT = "excel_font";
//  public static final String EXCEL_ALIGNMENT = "excel_alignment";
//  public static final String EXCEL_BORDER = "excel_border";
//  public static final String EXCEL_FILL = "excel_fill";


  // Collection display properties
  ///////////////////////////////////////////

  /**
   * Prevent new items from being inserted in the current or linked table.
   * Default is false.
   */
  public static final String NO_INSERT = "no_insert";

  /**
   * Prevent record from being modified. Default is false.
   */
  public static final String NO_MODIFY = "no_modify";

  /**
   * Prevent record from being deleted. Default is false.
   */
  public static final String NO_DELETE = "no_delete";

  /**
   * Prevent records from being copied. Default is false.
   */
  public static final String NO_COPY = "no_copy";

  /**
   * Prevent the column from being filtered. Default is false.
   */
  public static final String NO_FILTER = "no_filter";

  /**
   * Prevent the column or records of a table from being filtered. Default is false.
   */
  public static final String NO_EXPORT = "no_export";

  /**
   * Prevent the column from being sorted. Default is false.
   */
  public static final String NO_SORT = "no_sort";

  /**
   * For columns referring to a table this contains a subset of the columns
   * of the referred table that should be used to filter the data for display.
   * This is used for autocomplete on selectors, for instance.
   */
  public static final String FILTER_COLUMNS = "filter_columns";

  /**
   * Whether or not to control access to this member based on the current
   * security context. Default is true; if this is set to false, any access
   * control measures w.r.t the current member will be disabled. This is
   * required in certain cases where users in certain departments must have
   * unrestricted access to a particular member of a domain object while not
   * being granted full administrative privileges.
   */
  public static final String ACCESS_CONTROLLED = "access_controlled";

  // Lookup selector
  //////////////////////////////

  /**
   * The lookup containing the values that this column in restricted to.
   */
  public static final String LOOKUP = "lookup";

  /**
   * The table that this column is restricted to.
   */
  public static final String LINK_TABLE = "link_table";

  /**
   * A symbol for the field in the link table to use as the code.
   */
  public static final String LINK_CODE = "link_code";

  /**
   * A query expression executed against the link table to obtain the code label.
   */
  public static final String LINK_LABEL = "link_label";

  /**
   * A filter condition limiting the choices of the selector for this member.
   */
  public static final String LINK_WHERE = "LINK_WHERE";

  /**
   * Link table options as a json array of objects with two properties, code and label.
   * For instance,
   * <pre>
   * [
   *   {code: 'A', label: 'Option 1'},
   *   {code: 'B', label: 'Option 2'},
   *   {code: 'C', label: 'Option 3'}
   * ]
   * </pre>
   * This can also be provided as a JSON object with member names as code and their
   * value as the label, for a more succint representation:
   * <pre>
   * {
   *   A: 'Option 1',
   *   B: 'Option 2',
   *   C: 'Option 3'
   * }
   * </pre>
   * The object representation can further have an object value to support multi-language
   * labels:
   * <pre>
   * {
   *   A: {en: 'Option 1', fr: 'Choix 1'},
   *   B: {en: 'Option 2', fr: 'Choix 2'},
   *   C: {en: 'Option 3', fr: 'Choix 3'}
   * }
   * </pre>
   */
  public static final String VALUES = "values";

  /**
   * Whether to hide the code values for choices (not just the label).
   * Default is false for lookups and true for link tables.
   */
  public static final String HIDE_CODE = "hide_code";

  /**
   * An ordering for the items of a selector.
   */
  public static final String LINK_ORDER = "LINK_ORDER";

  /**
   * If this is set, a drop-down list with all distinct values for the column in
   * the table, subject to any filter_by conditions set on the column (if any).
   */
  public static final String SHOW_ALL_DISTINCT_VALUES = "show_all_distinct_values";

  /**
   * The label to show for a value. This is used when the value is
   * a code (or id) which requires replacement or embellishment for
   * display (e.g. 123 -&gt; 'Test server 1', 'MUS' -&gt; 'MUS - Mauritius')
   */
  public static final String VALUE_LABEL = "value_label";

  /**
   * An additional value associated with the selected option which
   * can be used by other fields referring to this field.
   */
  public static final String VALUE_NAME = "value_name";

  /**
   * When set to true on columns associated to lookups or other tables,
   * the full list of possible values for the column is not pre-loaded;
   * instead, as the user types, a list of highest matching options is
   * loaded and displayed.
   */
  public static final String ADAPTIVE = "adaptive";

  /**
   * Primary key info.
   */
  public static final String PRIMARY_KEY = "_primary_key";

  /**
   * Table and columns pointed by foreign keys on this table.
   */
  public static final String REFERENCES = "_references";

  /**
   * Array of tables and columns having foreign keys pointing to a column or
   * multiple columns in this table.
   */
  public static final String REFERRED_BY = "_referred_by";

  // Validation
  //////////////////////////

  /**
   * A mask for controlling input. The following characters are supported in the mask:
   * '#': A digit,
   * 'X': Any digit or letter,
   * 'S': Any letter,
   * 'A': Any letter, transformed to upper case,
   * 'a': Any letter, transformed to lower case,
   * '!': escapes the next character (appears as is)
   */
  public static final String MASK = "mask";

  /**
   * Whether or not a value is required for the member. If not set, the member
   * is deemed to be required if it is annotated with a @Display(required=TRUE).
   */
  public static final String REQUIRED = "required";

  /**
   * Members having unique set to TRUE cannot have duplicate values in the database, i.e,
   * they are candidate keys.
   */
  public static final String UNIQUE = "unique";

  /**
   * Validation check, as specified in check constraints.
   */
  public static final String CHECK = "_check";

  /**
   * Minimum length of the string representation of the value.
   */
  public static final String MIN_LENGTH = "min_length";

  /**
   * Maximum length of the string representation of the value of the member.
   */
  public static final String MAX_LENGTH = "max_length";

  /**
   * Minimum value for the value of a member of numeric type.
   */
  public static final String MINIMUM = "min";

  /**
   * Maximum value for the value of a member of numeric type.
   */
  public static final String MAXIMUM = "max";

  /**
   * Check if string value of the member matches an email pattern.
   */
  public static final String EMAIL = "email";

  /**
   * Check if string value of the member matches a phone pattern.
   */
  public static final String PHONE = "phone";

  /**
   * Matches the string representation of the member value to this
   * regular expression pattern.
   */
  public static final String PATTERN = "pattern";

  /**
   * Value type (date, number, text or boolean). This should take a valid type value (string,
   * date, datetime, etc.) with the difference from normal types being that this could be
   * dynamically enforced on a string type, e.g. For instance, a column with a string type
   * could have a value_type of datetime and this will be validated dynamically without causing
   * any database exception if the value is not in fact a datetime. This is useful when dealing
   * with data from untrusted sources such as when importing data into the system.
   */
  public static final String VALUE_TYPE = "value_type";

  /**
   * The error message to use when a validation error occurs instead of
   * the default ones.
   */
  public static final String VALIDATION_ERROR_MESSAGE = "validation_error_message";

  /**
   * A validation expression to apply to the value, returning null if the value is valid,
   * an error message otherwise.
   */
  public static final String VALIDATION = "validation";

  /**
   * Similar to validation but only warns users for such errors, with the option to proceed
   * irrespective.
   */
  public static final String WARN = "warn";
}
