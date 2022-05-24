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

function showSignUpAlert(msg) {
    document.getElementById("signUpErrorMsg").style.display = "block";
    document.getElementById("signUpErrorMsg").textContent = msg;
}

//Returns true if checks are OK
function pswErrorSignUp() {
    let psw1 = document.getElementById("pswSignUp").value;
    let psw2 = document.getElementById("psw2SignUp").value;

    if (psw1 === "") {
        showSignUpAlert("Enter a password...");
        return false;
    }
    else if (psw2 === "") {
        showSignUpAlert("Please enter the confirmation password...");
        return false;
    }

    // If not valid, return false
    else if (psw1 !== psw2) {
        showSignUpAlert("Password don't match.")
        return false;
    }
    else if (psw1.length < 6) {
        showSignUpAlert("Password is too short: at least 6 characters.");
        return false;
    }
    else {
        document.getElementById("signUpErrorMsg").style.display = "none";
        document.getElementById("signUpErrorMsg").textContent = "";
        return true;
    }
}

function mailAddress() {
    var pattern = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;

    if (pattern.test(document.getElementById("mailSignUp").value)) {
        document.getElementById("signUpErrorMsg").style.display = "none";
        return true;
    } else {
        showSignUpAlert("Input mail is invalid.");
        return false;
    }
}

//If user hits enter in a form, mimic a button click.

document.getElementById("formLogin").addEventListener("keypress", (e) => {
    if (e.key === "Enter") {
        e.preventDefault();
        document.getElementById("loginBtn").click();
    }
});

document.getElementById("formSignUp").addEventListener("keypress", (e) => {
    if (e.key === "Enter") {
        e.preventDefault();
        document.getElementById("signUpBtn").click();
    }
});



document.getElementById("showSignUpBtn").addEventListener("click", (e) => {
    showSignUp();
});

document.getElementById("showLoginBtn").addEventListener("click", (e) => {
    showLogin();
});

document.getElementById("loginBtn").addEventListener("click", (e) => {
    var formLogin = document.getElementById("formLogin");

    if (formLogin.checkValidity()) {//HTML settings 
        makeCall("POST", "CheckLogin", document.getElementById("formLogin"), function (req) {
            if (req.readyState === XMLHttpRequest.DONE) {
                var msg = req.responseText;
                switch (req.status) {
                    case 200: 
                        sessionStorage.setItem("user", msg);
                        window.location.href= "./home.html";
                        break;
                    default:
                        showLoginAlert(msg);
                        break;
                }
            }
        }
    );
    } else {
        formLogin.reportValidity(); //When false is returned, cancelable invalid events are fired for each invalid child and validation problems are reported to the user.
    }
});

document.getElementById("signUpBtn").addEventListener("click", (e) => {
    var form = document.getElementById("formSignUp");
    if (pswErrorSignUp()) {
        if (mailAddress()) {
            if (form.checkValidity()) {
                makeCall("POST", "SignUp", document.getElementById("formSignUp"), function (req) {
                    if (req.readyState === XMLHttpRequest.DONE) {
                        var msg = req.responseText;
                        switch (req.status) {
                            case 200:
                                sessionStorage.setItem("user", msg);
                                window.location.href = "./home.html";
                                break;
                            default:
                                showSignUpAlert(msg);
                                break;
                        }
                    }
                });
            } else {
                form.reportValidity();
            }
        }
    }
});
