(function (global) {
  'use strict';

  return function (success, error, image) {
    return cordova.exec(success, error, 'ImageCrop', 'crop', [image]);
  };
})(window);
