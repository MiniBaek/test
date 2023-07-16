package com.test;

import java.util.List;

public class RoutingInfo {

	private int port;
	private List<RouteInfo> routes;
	
	
	public RoutingInfo(int port, List<RouteInfo> routes) {
		super();
		this.port = port;
		this.routes = routes;
	}


	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	public List<RouteInfo> getRoutes() {
		return routes;
	}


	public void setRoutes(List<RouteInfo> routes) {
		this.routes = routes;
	}


	class RouteInfo{
		private String pathPrefix;
		private String url;
		public String getPathPrefix() {
			return pathPrefix;
		}
		public void setPathPrefix(String pathPrefix) {
			this.pathPrefix = pathPrefix;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public RouteInfo(String pathPrefix, String url) {
			super();
			this.pathPrefix = pathPrefix;
			this.url = url;
		}
		
	}
}
