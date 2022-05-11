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

//document.getElementById("pswSignUp").onchange = pswErrorSignUp;
//document.getElementById("psw2SignUp").onchange = pswErrorSignUp;

//Returns true if checks are OK
function pswErrorSignUp() {
    let psw1 = document.getElementById("pswSignUp").value;
    let psw2 = document.getElementById("psw2SignUp").value;

    if (psw1 === "") showPasswordAlert("Enter a password...");
    else if (psw2 === "") showPasswordAlert("Please enter the confirmation password...");

    // If not valid, return false
    else if (psw1 !== psw2) {
        showPasswordAlert("Password don't match.")
        return false;
    }
    else if (psw1.length < 6) {
        showPasswordAlert("Password is too short: at least 6 characters.");
        return false;
    }
    else {
        document.getElementById("pswErrorMsg").style.display = "none";
        document.getElementById("pswErrorMsg").textContent = "";
        return true;
    }
}

//e: event
(function () {

    document.getElementById("showSignUpBtn").addEventListener("click", (e) => {
        showSignUp();
    })

    document.getElementById("showLoginBtn").addEventListener("click", (e) => {
        showLogin();
    })

    document.getElementById("loginBtn").addEventListener("click", (e) => {
        var formLogin = document.getElementById("formLogin");

        if (formLogin.checkValidity()) {//HTML settings 
            makeCall("POST", "CheckLogin", document.getElementById("formLogin"), function (req) {
                if (req.readyState === XMLHttpRequest.DONE) {
                    var msg = req.responseText;
                    switch (req.status) {
                        case 200: 
                            sessionStorage.setItem("user", msg);
                            window.location.href= "./Home";
                            break;
                        default:
                            document.getElementById("loginErrorMsg").textContent = msg;
                            document.getElementById("loginErrorMsg").style.display = "block";
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
        if (pswErrorSignUp()) {
            var form = document.getElementById("formSignUp");
            if (form.checkValidity()) {
                makeCall("POST", "SignUp", document.getElementById("formSignUp"), function (req) {
                    if (req.readyState === XMLHttpRequest.DONE) {
                        var msg = req.responseText;
                        switch (req.status) {
                            case 200:
                                sessionStorage.setItem("user", msg);
                                window.location.href = "./Home";
                                break;
                            default:
                                document.getElementById("signUpErrorMsg").textContent = msg;
                                document.getElementById("signUpErrorMsg").style.display = "block";
                                break;
                        }
                    }
                });
            } else {
                form.reportValidity();
            }
        }
    });
})(); //IIFE