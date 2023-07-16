package mytest4.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import mytest4.info.DeviceResponseInfo;
import mytest4.info.ServerRequestInfo;
import mytest4.util.MyServletUtils;

public class MyDeviceServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) {

	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

		String reqURI = req.getRequestURI();
		String port = getInitParameter("port");
		String bodyJsonStr = MyServletUtils.getHttpBodyInString(req);

		if (reqURI.startsWith("/fromNode")) {

			//서버의 요청정보 : command, targetDevice, parameter
			ServerRequestInfo nodeRequestInfo = new Gson().fromJson(bodyJsonStr, ServerRequestInfo.class);

			// 서버응답설정
			DeviceResponseInfo deviceResult = new DeviceResponseInfo(nodeRequestInfo.getParam() + "_"+ port);
			String deviceResponseStr = new Gson().toJson(deviceResult);

			res.setStatus(200);
			res.getWriter().write(deviceResponseStr);
		}

	}
}
