/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Base
 *
 * @fileoverview
 *
 * @suppress {checkTypes}
 */

goog.provide('Base');

goog.require('Super');
goog.require('org_apache_flex_utils_Language');



/**
 * @constructor
 * @extends {Super}
 */
Base = function() {
  Base.base(this, 'constructor');
};
goog.inherits(Base, Super);


/**
 * @expose
 * @type {string}
 */
Base.prototype.text;

;


;Object.defineProperties(Base.prototype, /** @lends {Base.prototype} */ {
/** @expose */
text: {
get: /** @this {Base} */ function() {
  return "A" + text;
},
set: /** @this {Base} */ function(value) {
  if (value != text) {
    org_apache_flex_utils_Language.superSetter(Base, this, 'text', "B" + value);
  }
}}}
);


/**
 * Metadata
 *
 * @type {Object.<string, Array.<Object>>}
 */
Base.prototype.FLEXJS_CLASS_INFO = { names: [{ name: 'Base', qName: 'Base'}] };
