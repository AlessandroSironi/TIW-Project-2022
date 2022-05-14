/**
 * New Meeting Creation Manager
 */

( function() { //Avoid variables ending in the global scope

    function showErrorAlert(msg) {
        alert(msg);
        closeModal();
        refreshMeetings();
    }

    function showErrorCreationMeeting(msg) {
        document.getElementById("createNewMeetingError").style.display = "block";
        document.getElementById("createNewMeetingError").textContent = msg;
    }

    function showSuccessCreationMeeting(msg) {
        document.getElementById("createNewMeetingSuccess").style.display = "block";
        document.getElementById("createNewMeetingSuccess").textContent = msg;
    }

    function InvitationList (_alert, _list) {
        this.alert = _alert;
        this.list = _list;

        this.reset = function() {
            this.list.style.visibility = "hidden";
        };

        this.show = function() {
            let self = this; //Scope

            makeCall ("GET", "Registry", null, function (req) {
                if (req.readyState === XMLHttpRequest.DONE) {
                    var msg = req.responseText;
                    if (req.status == 200) {
                        var users = JSON.parse(req.responseText);
                        if (users.length == 0) {
                            showErrorAlert("Error: no other users to invite.");
                            return;
                        }
                        self.update(users); //Visible by closure.
                        sessionStorage.setItem("avaiableUsers", req.responseText);
                    } else {
                        showErrorAlert("Internal Error");
                    }
                }
            });

            this.update = function (invitationList) {
                var row, checkBoxCell, nameCell, surnameCell, mailCell;
                this.list.innerHtml = ""; //Empty content

                var self = this;

                invitationList.forEach(function (user) {
                    row = document.createElement("tr");
                    
                    checkBoxCell = document.createElement("input");
                    checkBoxCell.type = "checkbox";
                    checkBoxCell.className = "form-check-input";
                    checkBoxCell.value = user.Id;
                    row.appendChild(checkBoxCell);

                    nameCell = document.createElement("td");
                    nameCell.textContent = user.name;
                    row.appendChild(nameCell);

                    surnameCell = document.createElement("td");
                    surnameCell.textContent = user.surname;
                    row.appendChild(surnameCell);

                    mailCell = document.createElement("td");
                    mailCell.textContent = user.mail;
                    row.appendChild(mailCell);

                    self.list.appendChild(row);
                });

                this.list.style.visibility = "visible";
            }
        };
    }

    document.getElementById("createMeetingBtn").addEventListener("click", (e) => {
        var form = document.getElementById("newMeetingForm");
        if (form.checkValidity() && checkMeetingInfo()) {
            makeCall("POST", "CreateMeeting", form, function(req) {
                var msg = req.responseText;
                if (req.status == 200) {
                    setMeetingInfo(msg);
                    showModal();
                    document.getElementById("createNewMeetingError").style.display = "none";
                } else {
                    showErrorCreationMeeting("Error: the meeting could not be created.");
                }
            });
        } else {
            form.reportValidity();
        }
    });

    function getMeetingInfo() {
        const jsonString = sessionStorage.getItem("meeting");
        return JSON.parse(jsonString);
    }

    function setMeetingInfo (jsonMeetingInfo) {
        sessionStorage.setItem("meeting", jsonMeetingInfo);
    }

    function resetMeetingInfo () {
        sessionStorage.removeItem("meeting");
    }

    function checkMeetingInfo() {
        var form = document.getElementById("newMeetingForm");

        var dateInput = document.getElementById("dateNewMeetingInput");

        if (Date.parse(dateInput.value) < Date.now()) {
            showErrorCreationMeeting("The meeting's date must be future.");
            return false;
        } else {
            return true;
        }
    }

    function showModal() {

        $("#invitationModal").modal("show");
        
        let invitationList = new InvitationList (
            document.getElementById("modalAlertMsg"),
            document.getElementById("invitationsTableBody")
        );

        invitationList.reset();
        invitationList.show();
        document.getElementById("modalAlertMsg").style.display = "none";
    }

    function getInvitationAttempts () {
        return parseInt(sessionStorage.getItem("invitationAttempts"));
    }

    function incrementInvitationAttempts() {
        let temp = getInvitationAttempts();
        temp += 1;
        
        sessionStorage.setItem("invitationAttempts", temp.toString());
    }

    function showModalError(msg) {
        document.getElementById("modalAlertMsg").textContent = msg;
        document.getElementById("modalAlertMsg").style.display = "block";
    }

    document.getElementById("inviteModalBtn").addEventListener ("click", (e) => {
        var form = document.getElementById("invitationForm");
        var selectedUsers = getSelectedUsers();
        if (selectedUsers.length <= 0) {
            showModalError("Please select at least one user.");
        } else if (selectedUsers.length > getMeetingInfo().capacity) {
            incrementInvitationAttempts();
            let usersToRemove = selectedUsers.length - (getMeetingInfo().capacity);
            showModalError("Too many users selected. Please, deselect at least " + usersToRemove + "invitations.")
        } else {
            if (form.checkValidity()) {
                makeCall("POST", "CheckInvitations", form, function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        if (req.status == 200) {
                            showSuccessCreationMeeting("The meeting has been created.");
                            refreshMeetings();
                        }
                    }
                });
            } else {
                form.reportValidity();
            }
            return;
        } 
        if (getInvitationAttempts() >= 3) {
            refreshMeetings();
            showErrorAlert("Error: too many attempts to create a meeting with too many users.");
        }
    });

    function refreshMeetings() {
        //TODO: refresh meetingTables, close modal.
    }

    document.getElementById("closeModalBtn").addEventListener("click", (e) => {
        //delete meeting from sessionStorage and session tomcat servlet (?).
    });

})(); //IIFE