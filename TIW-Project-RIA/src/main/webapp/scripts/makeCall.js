/**
 * Make ASync call
 */

/* Generic function create to handle any request 
		method = "GET" or "POST"
		url = URL to send the request to
		formElement = form to send in body (if apply, null otherwise)
		cback = callback to invoke when status change to handle responses from server
		reset = if we use formElement then if we reset it or not
	*/

  function makeCall(method, url, formElement, cback, reset = true) {
    var req = new XMLHttpRequest(); // visible by closure
    req.onreadystatechange = function() {
        cback(req)
    }; // closure
    req.open(method, url);
    if (formElement == null) {
        req.send();
    } else if(formElement instanceof FormData) {
        req.send(formElement);
    } else {
        req.send(new FormData(formElement));
        if ( reset === true) {
            formElement.reset();
        }
    }
}
