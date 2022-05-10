/**
 * Login management - index.html
 */

function showLogin() {
    document.getElementById("loginDiv").style.display="block";
    document.getElementById("signUpDiv").style.display="none";
}

function showSignUp() {
    document.getElementById("loginDiv").style.display="none";
    document.getElementById("signUpDiv").style.display="block";
}

function showLoginAlert (msg) {
    document.getElementById("loginErrorMsg").style.display = "block";
    document.getElementById("loginErrorMsg").textContent = msg;
}

function showPasswordAlert(msg) {
    document.getElementById("pswErrorMsg").style.display = "block";
    document.getElementById("pswErrorMsg").textContent = msg;
}

function pswErrorSignUp() {
    let psw1 = document.getElementById("pswSignUp");
    let psw2 = document.getElementById("psw2SignUp");

    if (psw1 === "") showPasswordAlert("Enter a password...");
    else if (psw2 === "") showPasswordAlert("Please enter the confirmation password...");

    // If not valid, return false
    else if (psw1 !== psw2) {
        showPasswordAlert("Password did not match: please try again.")
        return false;
    }
    else if (psw1.length < 6) {
        showPasswordAlert("Password is too short: at least 6 characters.");
        return false;
    }
    else {
        return true;
    }

}