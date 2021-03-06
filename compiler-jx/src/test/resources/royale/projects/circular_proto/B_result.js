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
 * Generated by Apache Royale Compiler from B.as
 * B
 *
 * @fileoverview
 *
 * @suppress {missingRequire|checkTypes|accessControls}
 */

goog.provide('B');




/**
 * @constructor
 */
B = function() {
};


/**
 * Prevent renaming of class. Needed for reflection.
 */
goog.exportSymbol('B', B);


/**
 * @export
 * @param {boolean} b
 * @return {number}
 */
B.a = function(b) {
  if (b)
    return E.a(false);
  return 0;
};


/**
 * Metadata
 *
 * @type {Object.<string, Array.<Object>>}
 */
B.prototype.ROYALE_CLASS_INFO = { names: [{ name: 'B', qName: 'B', kind: 'class' }] };



/**
 * Reflection
 *
 * @return {Object.<string, Function>}
 */
B.prototype.ROYALE_REFLECTION_INFO = function () {
  return {
    variables: function () {return {};},
    accessors: function () {return {};},
    methods: function () {
      return {
        'B': { type: '', declaredBy: 'B'},
        '|a': { type: 'int', declaredBy: 'B', parameters: function () { return [  { index: 1, type: 'Boolean', optional: false } ]; }}
      };
    }
  };
};
