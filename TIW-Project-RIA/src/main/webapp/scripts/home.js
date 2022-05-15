/**
 *  Home management
 */

( function() {

    window.addEventListener("load", (e) => {
        if (sessionStorage.getItem("user") == null) {
            window.location.href = "./index.html";
        }
       //Navbar name loader
       const cookieName = document.cookie.split('; ').find(row => row.startsWith('name=')).split('=')[1];
       const cookieSurname = document.cookie.split('; ').find(row => row.startsWith('surname=')).split('=')[1];
       document.getElementById("navbarTextToFill").textContent = "Nice to see you, " + cookieName + " " + cookieSurname;
   
       meetingsCreated.show();
       meetingsInvited.show();
   });
   
   document.getElementById("logoutBtn").addEventListener("click", (e) => {
       window.location.href= "./Logout";
   });

    var meetingsCreated = new MeetingsCreated (
        document.getElementById("meetingsCreatedTable"),
        document.getElementById("meetingsCreatedBody")
    );
    
    var meetingsInvited = new MeetingsInvited (
        document.getElementById("meetingsInvitedTable"),
        document.getElementById("meetingsInvitedBody")
    );
    
      
    function MeetingsCreated (_meetingsCreatedTable, _meetingsCreatedBody) {
        this.meetingsCreatedTable = _meetingsCreatedTable;
        this.meetingsCreatedBody = _meetingsCreatedBody;

        this.show = function() {
            var self = this;

            makeCall("GET", "./GetMeetings", null, function(req) {
                if (req.readyState == XMLHttpRequest.DONE) {
                    var msg = req.responseText;
                    if (req.status == 200) {
                        var meetingsCreatedList = JSON.parse(req.responseText)[0];

                        if (meetingsCreatedList.length == 0) {
                            alert("You have not created any meetings.");
                            return;
                        } else { //If list is not empty...
                            self.update(meetingsCreatedList);
                        }
                    } else { //Request failed.
                        self.meetingsCreatedTable.style.visibility = "hidden";
                        alert("Unable to retriver data.");
                    }
                }
            })
        };

        this.update = function (meetingsCreatedArray) {
            var elem, i, row, title, date, duration, capacity; //TODO ?
            this.meetingsCreatedBody.innerHTML = ""; //Empty all data present.

            var self = this;
            meetingsCreatedArray.forEach(function (meetingCreated) { //Self is visible here, not this.
                row = document.createElement("tr");

                titleCell = document.createElement("td");
                titleCell.textContent = meetingCreated.title;
                row.appendChild(titleCell);

                dateCell = document.createElement("td");
                dateCell.textContent = parseDate(meetingCreated.startDate);
                row.appendChild(dateCell);

                durationCell = document.createElement("td");
                durationCell.textContent = meetingCreated.duration;
                row.appendChild(durationCell);

                capacityCell = document.createElement("td");
                capacityCell.textContent = meetingCreated.capacity;
                row.appendChild(capacityCell);

                //invitedByCell = document.createElement("td");
                //invitedByCell.textContent = meetingCreated.user_creator;
                //row.appendChild(invitedByCell);

                self.meetingsCreatedBody.appendChild(row);
            });

            this.meetingsCreatedBody.style.visibility = "visibile";
        }
    }

    function MeetingsInvited (_meetingsInvitedTable, _meetingsInvitedBody) {
        this.meetingsInvitedTable = _meetingsInvitedTable;
        this.meetingsInvitedBody = _meetingsInvitedBody;

        this.show = function() {
            var self = this;

            makeCall("GET", "./GetMeetings", null, function(req) {
                if (req.readyState == XMLHttpRequest.DONE) {
                    var msg = req.responseText;
                    if (req.status == 200) {
                        var meetingsInvitedList = JSON.parse(req.responseText)[1];

                        if (meetingsInvitedList.length == 0) {
                            alert("You have not been invited to any meetings.");
                            return;
                        } else { //If list is not empty...
                            self.update(meetingsInvitedList);
                        }
                    } else { //Request failed.
                        self.meetingsInvitedTable.style.visibility = "hidden";
                        alert("Unable to retriver data.");
                    }
                }
            })
        };

        this.update = function (meetingsInvitedArray) {
            var elem, i, row, titleCell, dateCell, durationCell, capacityCell, invitedByCell;
            this.meetingsInvitedBody.innerHTML = ""; //Empty all data present.

            var self = this;
            meetingsInvitedArray.forEach(function (meetingInvited) { //Self is visible here, not this.
                row = document.createElement("tr");

                titleCell = document.createElement("td");
                titleCell.textContent = meetingInvited.title;
                row.appendChild(titleCell);

                dateCell = document.createElement("td");
                dateCell.textContent = parseDate(meetingInvited.startDate);
                row.appendChild(dateCell);

                durationCell = document.createElement("td");
                durationCell.textContent = meetingInvited.duration;
                row.appendChild(durationCell);

                capacityCell = document.createElement("td");
                capacityCell.textContent = meetingInvited.capacity;
                row.appendChild(capacityCell);

                invitedByCell = document.createElement("td");
                invitedByCell.textContent = meetingInvited.user_Creator;
                row.appendChild(invitedByCell);

                self.meetingsInvitedBody.appendChild(row);
            });

            this.meetingsInvitedBody.style.visibility = "visibile";
        }
    }

    function parseDate (timestamp) {
        let tempDate = new Date (Date.parse(timestamp));
        let stringDate = parseDateAddZero(tempDate.getDay()) + "-" + parseDateAddZero(tempDate.getMonth()) + "-" + tempDate.getFullYear()
            + " " + parseDateAddZero(tempDate.getUTCHours()) + ":" + parseDateAddZero(tempDate.getMinutes()) + ":" + parseDateAddZero(tempDate.getSeconds());
        return stringDate;
    }

    function parseDateAddZero (value) {
        if (value < 10) {
            return "0" + value;
        } else {
            return value;
        }
    }

})();
