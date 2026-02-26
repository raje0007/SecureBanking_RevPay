package org.revpay.controller;

import org.revpay.model.Invoice;
import org.revpay.model.User;
import org.revpay.service.InvoiceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**

 * It handles user interaction related to:
 * - Creating invoices
 * - Viewing invoices
 * - Viewing outstanding invoices
 * - Paying invoices
 */
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final Scanner scanner;

    public InvoiceController() {
        this.invoiceService = new InvoiceService();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Show Invoice Dashboard (Business Users)
     */
    public void showInvoiceMenu(User businessUser) {

        if (!businessUser.isBusinessAccount()) {
            System.out.println("Access denied. Only business accounts can manage invoices.");
            return;
        }

        while (true) {
            System.out.println("\n==== RevPay Invoice Dashboard ====");
            System.out.println("1. Create Invoice");
            System.out.println("2. View All Invoices");
            System.out.println("3. View Outstanding Invoices");
            System.out.println("4. Back");
            System.out.print("Choose option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> handleCreateInvoice(businessUser);
                case 2 -> handleViewInvoices(businessUser);
                case 3 -> handleViewOutstandingInvoices(businessUser);
                case 4 -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Create new invoice
     */
    private void handleCreateInvoice(User businessUser) {

        try {
            System.out.print("Customer Name: ");
            String customerName = scanner.nextLine();

            System.out.print("Customer Email: ");
            String customerEmail = scanner.nextLine();

            System.out.print("Customer Phone: ");
            String customerPhone = scanner.nextLine();

            System.out.print("Total Amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

            System.out.print("Due Date (YYYY-MM-DD): ");
            LocalDate dueDate = LocalDate.parse(scanner.nextLine());

            Long invoiceId = invoiceService.createInvoice(
                    businessUser.getId(),
                    customerName,
                    customerEmail,
                    customerPhone,
                    amount,
                    dueDate
            );

            System.out.println("Invoice created successfully! ID: " + invoiceId);

        } catch (Exception e) {
            System.out.println("Invoice creation failed: " + e.getMessage());
        }
    }

    /**
     * View all invoices for business user
     */
    private void handleViewInvoices(User businessUser) {

        try {
            List<Invoice> invoices =
                    invoiceService.getInvoicesByBusinessUser(businessUser.getId());

            if (invoices.isEmpty()) {
                System.out.println("No invoices found.");
                return;
            }

            System.out.println("\n==== All Invoices ====");

            for (Invoice invoice : invoices) {
                printInvoice(invoice);
            }

        } catch (Exception e) {
            System.out.println("Failed to fetch invoices: " + e.getMessage());
        }
    }

    /**
     * View outstanding invoices
     */
    private void handleViewOutstandingInvoices(User businessUser) {

        try {
            List<Invoice> invoices =
                    invoiceService.getOutstandingInvoices(businessUser.getId());

            if (invoices.isEmpty()) {
                System.out.println("No outstanding invoices.");
                return;
            }

            System.out.println("\n==== Outstanding Invoices ====");

            for (Invoice invoice : invoices) {
                printInvoice(invoice);
            }

        } catch (Exception e) {
            System.out.println("Failed to fetch outstanding invoices: " + e.getMessage());
        }
    }

    /**
     * Utility method to print invoice details
     */
    private void printInvoice(Invoice invoice) {

        System.out.println("----------------------------------");
        System.out.println("Invoice Number: " + invoice.getInvoiceNumber());
        System.out.println("Customer: " + invoice.getCustomerName());
        System.out.println("Amount: " + invoice.getTotalAmount());
        System.out.println("Paid: " + invoice.getPaidAmount());
        System.out.println("Status: " + invoice.getStatus());
        System.out.println("Due Date: " + invoice.getDueDate());
        System.out.println("Remaining: " + invoice.getRemainingAmount());
    }
}