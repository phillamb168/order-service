package com.ewolff.microservice.order.logic;

import java.util.Collection;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Calendar;
import java.util.Date; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ewolff.microservice.order.clients.CatalogClient;
import com.ewolff.microservice.order.clients.Customer;
import com.ewolff.microservice.order.clients.CustomerClient;
import com.ewolff.microservice.order.clients.Item;

@Controller
class OrderController {

	private final Logger log = LoggerFactory.getLogger(CatalogClient.class);

	private OrderRepository orderRepository;

	private OrderService orderService;

	private CustomerClient customerClient;
	private CatalogClient catalogClient;

	private String version;

	// during development can set to true so that you dont
	// need to have the customer and catalog service running
	private boolean devMode;

	private static Random rand = new Random();

	@Autowired
	private OrderController(OrderService orderService,
			OrderRepository orderRepository, CustomerClient customerClient,
			CatalogClient catalogClient) {
		super();
		this.orderRepository = orderRepository;
		this.customerClient = customerClient;
		this.catalogClient = catalogClient;
		this.orderService = orderService;
		this.version = System.getenv("APP_VERSION");
		this.devMode = Boolean.parseBoolean(System.getenv("DEV_MODE"));

		log.debug("Initial APP_VERSION: " + this.version);
		System.out.println("Initial APP_VERSION: " + this.version);
		System.out.println("devMode: " + Boolean.toString(devMode));
	}

	private String getVersion() {
		System.out.println("Current APP_VERSION: " + this.version);
		return this.version;
	}

	private void setVersion(String newVersion) {
		this.version = newVersion;
		System.out.println("Setting APP_VERSION to: " + this.version);
	}

	private void throwException() throws Exception {
		System.out.println("Throwing fake exception");
		throw new Exception("Throwing fake exception");
	}

	@ModelAttribute("items")
	public Collection<Item> items() {
		if(devMode) {
			System.out.println("Get Items using fake data");
			ArrayList <Item>items = new ArrayList<Item>();
			items.add(new Item(1, "Item 1", 100.00));
			items.add(new Item(2, "Item 2", 200.00));
			items.add(new Item(3, "Item 3", 300.00));
			return items;	
		}
		else
		{
			return catalogClient.findAll();
		}
	}

	@ModelAttribute("customers")
	public Collection<Customer> customers() {
		if(devMode) {
			System.out.println("Get Customers using fake data");
			Collection<Customer> allCustomers = new ArrayList<Customer>();
			allCustomers.add(new Customer(1, "Dummy", "Customer1", "customer1@dummy.com", "1 Elm Street", "NYC"));
			allCustomers.add(new Customer(2, "Dummy", "Customer2", "customer2@dummy.com", "1 Elm Street", "NYC"));
			return allCustomers;
		}
		else
		{
			if (this.getVersion().equals("2")) {
				System.out.println("N+1 problem = ON");
				Collection<Customer> allCustomers = customerClient.findAll();
				// ************************************************
				// N+1 Problem
				// Add additional lookups for each customer
				// this will cause additional SQL calls
				// ************************************************
				Iterator<Customer> itr = allCustomers.iterator();
				while (itr.hasNext()) {
					Customer cust = itr.next();
					long id = cust.getCustomerId();
					for(int i=1; i<=20; i++){
						customerClient.getOne(id);
					}
				}
				return allCustomers;
			}
			else {
				System.out.println("N+1 problem = OFF");
				return customerClient.findAll();
			}
		}
	}

	@RequestMapping("/")
	public ModelAndView orderList() throws Exception {

		if (this.getVersion().equals("3")) {
			this.throwException();
		}
		return new ModelAndView("orderlist", "orders",
				orderRepository.findAll());
	}

	@RequestMapping(value = "/form.html", method = RequestMethod.GET)
	public ModelAndView form() throws Exception {

		if (this.getVersion().equals("3")) {
			this.throwException();
		}
		
		return new ModelAndView("orderForm", "order", new Order());
	}

	@RequestMapping(value = "/line", method = RequestMethod.POST)
	public ModelAndView addLine(Order order) throws Exception {

		if (this.getVersion().equals("3")) {
			this.throwException();
		}

		if (this.getVersion().equals("2")) {
			System.out.println("Order Line Exception Problem = ON");
			// ************************************************
			// in 50% of the cases will return incorrect data
			// back resulting in a 500 error in the UI
			// ************************************************
			int n = rand.nextInt(100);
			System.out.println("Random Number: " + Integer.toString(n));
			if(n < 50) {
				System.out.println("ADDING A NULL FOR ORDER!!");
				return new ModelAndView("orderForm", "order", null);
			}
		}
		if(devMode) {
			System.out.println("Get Order Line using fake data");
			order.addLine(0, 1);
			return new ModelAndView("orderForm", "order", order);
		}
		else
		{
			System.out.println("Order Line Exception Problem = OFF");
			order.addLine(0, catalogClient.findAll().iterator().next().getItemId());
			return new ModelAndView("orderForm", "order", order);
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView get(@PathVariable("id") long id) throws Exception {

		if (this.getVersion().equals("3")) {
			this.throwException();
		}
		return new ModelAndView("order", "order", orderRepository.findById(id).get());
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ModelAndView post(Order order) throws Exception {

		if (this.getVersion().equals("3")) {
			this.throwException();
		}

		order = orderService.order(order);
		return new ModelAndView("success");
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ModelAndView post(@PathVariable("id") long id) throws Exception {

		if (this.getVersion().equals("3")) {
			this.throwException();
		}
		orderRepository.deleteById(id);

		return new ModelAndView("success");
	}

   @RequestMapping(value = "/version", method = RequestMethod.GET)
   @ResponseBody
   public String showVersion() {
		String version;
		try {
			version = this.getVersion();
		}
		catch(Exception e) {
			version = "APP_VERSION not found";
		}
		return version;
   } 

	@RequestMapping(value = "setversion/{version}", method = RequestMethod.GET)
	public ModelAndView webSetVersion(@PathVariable("version") String newVersion) {
		this.setVersion(newVersion);
		return new ModelAndView("success");
	}

	@RequestMapping(value = "/health", method = RequestMethod.GET)
	@ResponseBody
	public String getHealth() {

		Date dateNow = Calendar.getInstance().getTime();
		String health = "{ \"health\":[{\"service\":\"order-service\",\"status\":\"OK\",\"date\":\"" + dateNow + "\" }]}";
		return health;
	}
}
