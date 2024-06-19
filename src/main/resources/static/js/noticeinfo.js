// HTML로딩이 완료되고, 실행됨
$(document).ready(function () {
    // 버튼 클릭했을때, 발생되는 이벤트 생성함(onclick 이벤트와 동일함)
    $("#btnEdit").on("click", function () {
        doEdit(); // 공지사항 수정하기 실행
    })

    // 버튼 클릭했을때, 발생되는 이벤트 생성함(onclick 이벤트와 동일함)
    $("#btnDelete").on("click", function () {
        doDelete(); // 공지사항 수정하기 실행
    })

    // 버튼 클릭했을때, 발생되는 이벤트 생성함(onclick 이벤트와 동일함)
    $("#btnList").on("click", function () {
        location.href = "/notice/noticeList"; // 공지사항 리스트 이동
    })

    $("#btnCInst").on("click", function () {
        doCInsert();
    })

    let textBox = document.getElementById("textContent")
    textBox.innerText = textContent

    if (cList) {

        for (let i = 0; i < cList.length; i++) {

            let commentSeq = getCommentSeq(cList[i])
            let commentText = getCommentContent(cList[i])

            console.log(i + "번째 코멘트 번호 : " + commentSeq + "번, 내용 : " + commentText)


            let commentBox = document.getElementById("commentContent" + commentSeq)

            commentBox.innerText = commentText

        }
    }
})

// 수정하기
function doEdit() {
    if (session_user_id === user_id) {
        location.href = "/notice/noticeEditInfo?nSeq=" + nSeq;

    } else if (session_user_id === "") {
        alert("로그인 하시길 바랍니다.");

    } else {
        alert("본인이 작성한 글만 수정 가능합니다.");

    }
}

//삭제하기
function doDelete() {
    if (session_user_id === user_id) {
        if (confirm("작성한 글을 삭제하시겠습니까?")) {

            // Ajax 호출해서 글 삭제하기
            $.ajax({
                    url: "/notice/noticeDelete",
                    type: "post", // 전송방식은 Post
                    dataType: "JSON", // 전송 결과는 JSON으로 받기
                    data: {"nSeq": nSeq}, // form 태그 내 input 등 객체를 자동으로 전송할 형태로 변경하기
                    success:
                        function (json) { // /notice/noticeDelete 호출이 성공했다면..
                            alert(json.msg); // 메시지 띄우기
                            location.href = "/notice/noticeList"; // 공지사항 리스트 이동
                        }
                }
            )
        }

    } else if (session_user_id === "") {
        alert("로그인 하시길 바랍니다.");

    } else {
        alert("본인이 작성한 글만 수정 가능합니다.");

    }
}

//댓글 작성
function doCInsert() {

    let content = $('textarea[name="content"]').val();

    if (content === "") {
        alert("댓글 내용을 입력하세요.");
        cContent.focus();
        return;
    }

    if (calBytes(content) > 150) {
        alert("최대 150Bytes까지 입력 가능합니다.");
        cContent.focus();
        return;
    }

    let noticeSeq = document.querySelector('div[name="noticeSeq"]').innerText;

    console.log("content : " + content)
    console.log("noticeSeq : " + noticeSeq)

    if (session_user_id) {

        //
        $.ajax({
                url: "/comment/commentInsert",
                type: "post", // 전송방식은 Post
                dataType: "JSON", // 전송 결과는 JSON으로 받기
                data: {
                    "content": content,
                    "noticeSeq": noticeSeq
                }, // form 태그 내 input 등 객체를 자동으로 전송할 형태로 변경하기
                success:
                    function (json) { //  호출이 성공했다면..
                        alert(json.msg); // 메시지 띄우기
                        location.reload(); // 공지사항 리스트 이동
                    }
            }
        )

    } else if (session_user_id === "") {

        alert("로그인 하시길 바랍니다.");
        location.href = "/user/login";

    } else {

        alert("알 수 없는 오류가 발생했습니다.")
        location.href = "/allergy/main";

    }
}


//삭제하기
function doCDel(f) {

    // 게시글 번호
    let noticeSeq = nSeq;

    // commentSeq(f)에 따른 div값들 조회
    let commentBox = document.getElementById('comment' + f);

    // commentBox 내부에서 name이 "cUserId"인 요소 선택 및 값 가져오기
    let cUserId = commentBox.querySelector('.comment-name[name="cUserId"]').innerText;


    console.log("cUserId : " + cUserId)
    console.log("commentSeq : " + f)
    console.log("noticeSeq : " + nSeq)

    if (session_user_id === cUserId) {
        if (confirm("작성한 댓글을 삭제하시겠습니까?")) {

            // Ajax 호출해서 글 삭제하기
            $.ajax({
                    url: "/comment/commentDelete",
                    type: "post", // 전송방식은 Post
                    dataType: "JSON", // 전송 결과는 JSON으로 받기
                    data: {
                        "commentSeq": f,
                        "noticeSeq": noticeSeq
                    }, // form 태그 내 input 등 객체를 자동으로 전송할 형태로 변경하기
                    success:
                        function (json) { // /comment/commentDelete 호출이 성공했다면..
                            alert(json.msg); // 메시지 띄우기
                            location.reload(); // 화면 새로고침
                        }
                }
            )
        }

    } else if (session_user_id === "") {
        alert("로그인 하시길 바랍니다.");

    } else {
        alert("본인이 작성한 댓글만 수정 가능합니다.");
    }
}

function doCEdit(commentSeq) {
    // 해당 댓글의 commentSeq 값을 가져옵니다.

    let editButtonId = 'comment_edit' + commentSeq;
    let delButtonId = 'comment-del' + commentSeq;

    // 수정 버튼을 "수정하기"로 변경합니다.
    document.getElementById(editButtonId).innerText = '수정하기';
    document.getElementById(editButtonId).setAttribute('onclick', 'doCUpdate(' + commentSeq + ')');

    // 삭제 버튼을 "취소"로 변경합니다.
    document.getElementById(delButtonId).innerText = '취소';
    document.getElementById(delButtonId).setAttribute('onclick', 'doCancel()');

    // 댓글 내용(id="'commentContent' +${dto.commentSeq()}")을 textarea로 변경합니다.
    let commentContentId = 'commentContent' + commentSeq;
    let commentContentElement = document.getElementById(commentContentId);
    let commentText = commentContentElement.innerText; // 이전에 있는 텍스트 가져오기
    let textareaElement = document.createElement('textarea'); // 새로운 textarea 요소 생성
    textareaElement.value = commentText; // textarea에 이전 텍스트를 설정
    textareaElement.setAttribute('id', commentContentId); // textarea에 id 설정
    textareaElement.setAttribute('name', 'commentContent'); // textarea에 name 설정
    textareaElement.style.width = '100%'; // textarea에 width: 100% 스타일 설정
    commentContentElement.parentNode.replaceChild(textareaElement, commentContentElement); // 기존 댓글 내용 요소를 textarea로 교체

}

function doCUpdate(commentSeq) {

    let noticeSeq = nSeq;
    // commentSeq(f)에 따른 div값들 조회
    let commentBox = document.getElementById('comment' + commentSeq);

    // commentBox 내부에서 name이 "cUserId"인 요소 선택 및 값 가져오기
    let cUserId = commentBox.querySelector('.comment-name[name="cUserId"]').innerText;

    let commentContentId = 'commentContent' + commentSeq;
    let commentContentElement = document.getElementById(commentContentId);
    let commentContents = commentContentElement.value; // 현재 텍스트 가져오기

    if (commentContents === "") {
        alert("댓글 내용을 입력하세요.");
        commentContentElement.focus();
        return;
    }

    if (calBytes(commentContents) > 150) {
        alert("최대 150Bytes까지 입력 가능합니다.");
        commentContentElement.focus();
        return;
    }

    console.log("cUserId : " + cUserId)
    console.log("commentSeq : " + commentSeq)
    console.log("commentContents : " + commentContents)

    if (session_user_id === cUserId) {
        if (confirm("작성한 댓글을 수정하시겠습니까?")) {

            // Ajax 호출해서 글 삭제하기
            $.ajax({
                    url: "/comment/commentUpdate",
                    type: "post", // 전송방식은 Post
                    dataType: "JSON", // 전송 결과는 JSON으로 받기
                    data: {
                        "commentSeq": commentSeq,
                        "noticeSeq": noticeSeq,
                        "commentContents": commentContents
                    }, // form 태그 내 input 등 객체를 자동으로 전송할 형태로 변경하기
                    success:
                        function (json) { // /comment/commentDelete 호출이 성공했다면..
                            alert(json.msg); // 메시지 띄우기
                            location.reload(); // 화면 새로고침
                        }
                }
            )
        }

    } else if (session_user_id === "") {
        alert("로그인 하시길 바랍니다.");

    } else {
        alert("본인이 작성한 댓글만 수정 가능합니다.");
    }

}

function doCancel(commentSeq) {
    location.reload();
}

function getCommentSeq(dto) {
    // monthSales가 메서드일 경우 호출하고, 속성일 경우 그대로 반환합니다.
    return typeof dto.commentSeq === 'function' ? dto.commentSeq() : dto.commentSeq;
}

function getCommentContent(dto) {
    // monthSales가 메서드일 경우 호출하고, 속성일 경우 그대로 반환합니다.
    return typeof dto.commentContents === 'function' ? dto.commentContents() : dto.commentContents;
}

// 글자 수 체크
function calBytes(str) {
    let tcount = 0;
    let tmpStr = String(str);
    let strCnt = tmpStr.length;
    let onechar;
    for (let i = 0; i < strCnt; i++) {
        onechar = tmpStr.charAt(i);
        if (escape(onechar).length > 4) {
            tcount += 2;
        } else {
            tcount += 1;
        }
    }
    return tcount;
}
