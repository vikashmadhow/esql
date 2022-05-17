/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.builder;

/**
 * A standard set metadata of attributes which can be attached to tables and
 * fields through their metadata definition.
 * <p>
 * Attributes starting with display_ are interpreted on the client side. They
 * are packaged into a javascript function as follows:
 *
 * <pre>
 * function(value, context) {
 *   [return] (attribute value goes here)
 * }
 * </pre>
 * <p>
 * The attribute value becomes the body of the function and has the current
 * value of the object (column or row) and a context object which contains more
 * information related to the object value and the current environment.
 * <p>
 * Through the context argument, the function has access to the row which the
 * object value is part of (if applicable); if this row was part of a row array,
 * the latter is supplied as the rows property of the context. The column index
 * of the value in the row and the row index of the row in the rows array are
 * supplied as the columnIndex and rowIndex properties, respectively.
 * Additionally, the context may contain information on what is being done
 * currently with the object value (e.g., edited, displayed in a data table,
 * etc.)
 * <p>
 * If the attribute value is a single expression without a 'return' statement
 * element, an implicit return will be added. Otherwise, the attribute value is
 * inserted as-is, and the system assumes that the value returns something
 * explicitly. The detection of whether the attribute is a single expression is
 * quite basic: the system checks for the presence of ';' and 'return'. If those
 * two substrings are not found, it is assumed that the attribute value is an
 * expression and an implicit return is added.
 * <p>
 * Attribute values are thus computed at runtime and can dynamically control
 * various aspects of the user-interface, such as when the visibility of
 * certain fields in relation to values of other fields, validation rules, etc.
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

  /*
   * An initial value to set on a field on creation.
   */
  public static final String INITIAL_VALUE = "initial_value";

  public static final String ID = "_id";

  /**
   * Contains the type of attribute.
   */
  public static final String TYPE = "type";

  /**
   * The expression to compute as the default value for a column when
   * no value is specified in an insert statement, and the expression
   * to compute the value for a derived column.
   */
  public static final String EXPRESSION = "expression";

  /**
   * The name of a column containing a sequence number to order a set
   * rows by.
   */
  public static final String SEQUENCE_COLUMN = "sequence_column";

  /**
   * The name a column that will contain the id of a parent row.
   */
  public static final String PARENT_LINK_COLUMN = "link_column";

  /*
   * Set to true on derived fields.
   */
  public static final String DERIVED = "derived";

// DEFAULT_VALUE is now used for this purpose
//  /*
//   * The expression for derived fields
//   */
//  public static final String DERIVED_EXPRESSION = "derived_expression";

  /*
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
  public static final String READ_ONLY = "read_only";

  /**
   * If computes to false, field is not shown. Default is true.
   */
  public static final String SHOW = "show";

  /**
   * If computes to false, field is not shown when editing. Default is true.
   */
  public static final String EDIT = "edit";

  /**
   * If computes to false, field is not shown when browsing. Default is true.
   */
  public static final String BROWSE = "browse";


  // File upload and download
  ///////////////////////////////////////////////////////

  /**
   * If set to true, files can be uploaded into this column as
   * base64 text data. File metadata can be placed in other columns
   * specified in other attributes (see next).
   */
  public static final String FILE = "file";

  /**
   * If set to true, files can be uploaded into this column as
   * raw binary data and stored as an external file (outside the
   * database). The file can be downloaded through a provided link.
   */
  public static final String EXTERNAL_FILE = "external_file";

  /*
   * The column in which to store the file size.
   */
  public static final String FILE_SIZE_COLUMN = "file_size_column";

  /*
   * The column in which to store the file mime type.
   */
  public static final String FILE_TYPE_COLUMN = "file_type_column";

  /*
   * The column in which to store the file name.
   */
  public static final String FILE_NAME_COLUMN = "file_name_column";


  /**
   * The order in which this member should appear when the object is being
   * edited/viewed. Fields with lower sequence numbers are displayed before
   * those with higher ones. Fields without a sequence number will be given
   * one based on the order that they were added to the table. New fields
   * added through ALTER TABLE are always added at the end of the table.
   */
  public static final String SEQUENCE = "sequence";

//    /**
//     * When set on a column, marks it as containing a value which can be used
//     * to order the records of the table.
//     */
//    public static final String SEQUENCER = "sequencer";

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
   * One or more CSS classes to apply to member.
   */
  public static final String CSS_CLASSES = "css_classes";

  /**
   * A set of CSS styles to apply directly to the member.
   */
  public static final String STYLE = "style";

  /**
   * A width, in pixel, to apply to the component displaying this member.
   * This width may be ignored when its application will break the layout
   * of the display. The width can also be one of the several constant
   * responsive widths which would allow the system to manage the size
   * of the control in response to changes in the size of the client
   * viewport.
   */
  public static final String WIDTH = "width";

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

  public static final String EXCEL_FONT = "excel_font";
  public static final String EXCEL_ALIGNMENT = "excel_alignment";
  public static final String EXCEL_BORDER = "excel_border";
  public static final String EXCEL_FILL = "excel_fill";


  // Collection display properties
  ///////////////////////////////////////////

  /**
   * Can a new item be inserted in the collection? default = false
   */
  public static final String CAN_INSERT = "can_insert";

  /**
   * Can item values in the list be edited? default = false
   */
  public static final String CAN_EDIT = "can_edit";

  /**
   * Can items be deleted from the list? default = false
   */
  public static final String CAN_REMOVE = "can_remove";

  /*
   * Can records be deleted.
   */
  public static final String CAN_DELETE = "can_delete";

  /**
   * Can items in the list be copied? default = false. When set to false, this also prevents
   * a deep copy of object to be made.
   */
  public static final String CAN_COPY = "can_copy";

  /**
   * Should the list provide filtering controls.
   */
  public static final String CAN_FILTER = "can_filter";

  /**
   * Should the collection provide export controls.
   */
  public static final String CAN_EXPORT = "can_export";

  /**
   * Can item values in the list be updated? default = false
   */
  public static final String CAN_UPDATE = "can_update";

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
   * The name of the lookup (which must not contain any periods) or a foreign
   * table which is used to obtain the display value for this field.
   */
  public static final String LINK_TABLE = "link_table";

  /**
   * A symbol for the field in the link table to use as the code.
   */
  public static final String LINK_TABLE_CODE_VALUE = "link_table_code_value";

  /**
   * A query expression executed against the link table to obtain the code label.
   */
  public static final String LINK_TABLE_CODE_LABEL = "link_table_code_label";

  /**
   * A query expression executed against the link table to obtain the code name.
   * This can also be the name of a column in the link table to use as the code name.
   */
  public static final String LINK_TABLE_CODE_NAME = "link_table_code_name";

  /**
   * Link table options as a json array of objects with two properties, value and label.
   * For instance,
   * [
   * {value: 1, label: 'Option 1'},
   * {value: 2, label: 'Option 2'},
   * {value: 3, label: 'Option 3'}
   * ]
   */
  public static final String LINK_TABLE_OPTIONS = "link_table_options";

  /**
   * The link table options as map from the values to their labels.
   * This is used to display the label and set the VALUE_LABEL when
   * the selection is changed.
   */
  public static final String LINK_TABLE_OPTIONS_MAP = "link_table_options_map";


  /**
   * Whether or not to show the code values for choices (not just the label).
   * Default is true.
   */
  public static final String LINK_TABLE_SHOW_CODE = "link_table_show_code";

  /**
   * If this is set, a drop-down list with all distinct values in the
   * database (subject to condition() for this member is
   * displayed for selection.
   */
  public static final String SHOW_ALL_DISTINCT_VALUES = "show_all_distinct_values";

  /**
   * The label to show for a value. This is used when the value is
   * a code (or id) which requires replacement or embellishment for
   * display (e.g. 123 -&gt; 'Test server 1', 'MUS' -&gt; 'MUS - Mauritius'
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

  // Event script
  /////////////////////////////
  /*  Can be used to associate client-side script (such as javascript)
   *  with an html event (such as onClick) for a member. */

  /**
   * The event to associate client-side code to.
   */
  public static final String CLIENT_SIDE_EVENT = "client_side_event";

  /**
   * Associate client-side code. The code can contain the {@code ${<member_path>}}string
   * which will be replaced by the actual string value of the member path.
   */
  public static final String CLIENT_SIDE_SCRIPT = "client_side_script";

  /**
   * The candidate class over which to perform any query. It is almost
   * never required to explicitly provide a value for this member since
   * this is taken to be the class of the member to which this @Display
   * annotation is attached; however, in certain cases such as when the
   * class is dynamically generated, this must be specified so that
   * queries will not break.
   * <p>
   * This is also required for multiple entity selection. Multiple entity
   * selection is stored as a comma-separated list in a string, therefore
   * the type of the member cannot be used to determine the domain object
   * class to search. Therefore, the latter must be explicitly specified
   * in this member of the @Display annotation set on the member.
   */
  public static final String CANDIDATE_TABLE = "candidate_table";

  /**
   * A filter condition limiting the choices of the selector for this member.
   */
  public static final String FILTER_BY = "filter_by";

  /**
   * A ordering for the items of a selector.
   */
  public static final String ORDER_BY = "order_by";

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
  public static final String CHECK = "check";

  /**
   * Primary key info
   */
  public static final String PRIMARY_KEY = "primary_key";

  /**
   * Table and columns pointed by foreign keys on this table.
   */
  public static final String REFERENCES = "references";

  /**
   * Array of tables and columns having foreign keys pointing to a column or
   * multiple columns in this table.
   */
  public static final String REFERRED_BY = "referred_by";

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
  public static final String MINIMUM = "minimum";

  /**
   * Maximum value for the value of a member of numeric type.
   */
  public static final String MAXIMUM = "maximum";

  // temporally deprecated: minimum and maximum used for before and after
  // Could be re-enabled if a genuine use case is found
//    /**
//     * date value < now.
//     */
//    public static final String PAST = "past";
//
//    /**
//     * date value <= now.
//     */
//    public static final String PAST_OR_NOW = "past_or_now";
//
//    /**
//     * Checks if the date value of the member is before the specified date.
//     */
//    public static final String BEFORE = "before";
//
//    /**
//     * Checks if the date value of the member is after the specified date.
//     */
//    public static final String AFTER = "after";

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
