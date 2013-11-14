goog.provide('Super');



/**
 * @constructor
 */
Super = function() {
};


/**
 * @private
 * @type {string}
 */
Super.prototype._text = '';


/**
 * @expose
 * @return {string}
 */
Super.prototype.get_text = function() {
  return this._text;
};


/**
 * @expose
 * @param {string} value
 */
Super.prototype.set_text = function(value) {
  if (value != this._text) {
    this._text = value;
  }
};