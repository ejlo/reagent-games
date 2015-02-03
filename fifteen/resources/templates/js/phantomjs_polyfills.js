(function() {

var Ap = Array.prototype;
var slice = Ap.slice;
var Fp = Function.prototype;

if (!Fp.bind) {
  // PhantomJS doesn't support Function.prototype.bind natively, so
  // polyfill it whenever this module is required.
  Fp.bind = function(context) {
    var func = this;
    var args = slice.call(arguments, 1);

    function bound() {
      var invokedAsConstructor = func.prototype && (this instanceof func);
      return func.apply(
        // Ignore the context parameter when invoking the bound function
        // as a constructor. Note that this includes not only constructor
        // invocations using the new keyword but also calls to base class
        // constructors such as BaseClass.call(this, ...) or super(...).
        !invokedAsConstructor && context || this,
        args.concat(slice.call(arguments))
      );
    }

    // The bound function must share the .prototype of the unbound
    // function so that any object created by one constructor will count
    // as an instance of both constructors.
    bound.prototype = func.prototype;

    return bound;
  };
}


  // Patch since PhantomJS does not implement click() on HTMLElement. In some
  // cases we need to execute the native click on an element. However, jQuery's
  // $.fn.click() does not dispatch to the native function on <a> elements, so we
  // can't use it in our implementations: $el[0].click() to correctly dispatch.
if (!HTMLElement.prototype.click) {
  HTMLElement.prototype.click = function() {
    var ev = document.createEvent('MouseEvent');
    ev.initMouseEvent(
      'click',
      /*bubble*/true, /*cancelable*/true,
      window, null,
      0, 0, 0, 0, /*coordinates*/
      false, false, false, false, /*modifier keys*/
      0/*button=left*/, null
    );
    this.dispatchEvent(ev);
  };
}

})();
