package lucentglassmanagement;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomerDashboard extends JFrame {

    private static final Color GREEN = new Color(28, 75, 58);
    private static final Color DARK_GREEN = new Color(19, 53, 41);
    private static final Color CREAM = new Color(248, 245, 235);
    private static final Color IVORY = new Color(255, 253, 247);
    private static final Color MUTED = new Color(105, 115, 106);
    private static final Color GOLD = new Color(190, 151, 82);

    private final int customerId;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private JLabel welcomeLabel;
    private JLabel profileDetailsLabel;
    private JPanel productGrid;
    private JTextField productSearch;
    private JComboBox<String> categoryFilter;
    private JButton cartTopButton;
    private JTable cartTable, ordersTable;
    private JPanel trackingCards;
    private final DecimalFormat money = new DecimalFormat("#,##0.00");
    private final ArrayList<CartItem> cart = new ArrayList<>();

    public CustomerDashboard(int customerId) {
        this.customerId = customerId;
        setTitle("Lucent Glass Industries | Customer Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 650));
        setLayout(new BorderLayout());
        getContentPane().setBackground(CREAM);

        add(createSidebar(), BorderLayout.WEST);
        add(createMainArea(), BorderLayout.CENTER);

        loadCustomerName();
        showPage("dashboard");
        // Keep the customer order list and tracking view in sync with admin/production updates.
        new Timer(10000, e -> {
            if (ordersTable != null) refreshOrders();
            if (trackingCards != null) refreshTracking();
        }).start();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(235, 0));
        sidebar.setBackground(DARK_GREEN);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(25, 17, 22, 17));

        JLabel brand = new JLabel("LUCENT");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Georgia", Font.BOLD, 27));
        JLabel subBrand = new JLabel("GLASS INDUSTRIES");
        subBrand.setForeground(new Color(211, 220, 207));
        subBrand.setFont(new Font("Segoe UI", Font.BOLD, 10));

        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(3));
        sidebar.add(subBrand);
        sidebar.add(Box.createVerticalStrut(38));

        JLabel menuTitle = new JLabel("CUSTOMER MENU");
        menuTitle.setForeground(new Color(177, 196, 180));
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        sidebar.add(menuTitle);
        sidebar.add(Box.createVerticalStrut(13));

        addMenuButton(sidebar, "⌂", "Dashboard", "dashboard");
        addMenuButton(sidebar, "◇", "Order Glass", "products");
        addMenuButton(sidebar, "▣", "My Cart", "cart");
        addMenuButton(sidebar, "▤", "My Orders", "orders");
        addMenuButton(sidebar, "➜", "Track Orders", "tracking");
        addMenuButton(sidebar, "○", "My Profile", "profile");

        sidebar.add(Box.createVerticalGlue());

        JLabel help = new JLabel("<html>Need assistance?<br>Contact our customer support team.<br><br>" +
                "<b>lucentglassindustries@gmail.com</b></html>");
        help.setForeground(new Color(216, 224, 213));
        help.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sidebar.add(help);
        sidebar.add(Box.createVerticalStrut(17));

        JButton logout = new JButton("↪   Logout");
        styleMenuButton(logout, true);
        logout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?", "Logout",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        sidebar.add(logout);
        return sidebar;
    }

    private void addMenuButton(JPanel sidebar, String symbol, String text, String page) {
        JButton button = new JButton(symbol + "    " + text);
        styleMenuButton(button, false);
        button.addActionListener(e -> showPage(page));
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(7));
    }

    private void styleMenuButton(JButton button, boolean logout) {
        button.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(logout ? new Color(91, 65, 51) : DARK_GREEN);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 13, 12, 8));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!logout) button.setBackground(GREEN);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!logout) button.setBackground(DARK_GREEN);
            }
        });
    }

    private JPanel createMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(CREAM);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(IVORY);
        top.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(226, 222, 208)),
                new EmptyBorder(20, 30, 18, 30)));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        welcomeLabel = new JLabel("Welcome to Lucent");
        welcomeLabel.setFont(new Font("Georgia", Font.BOLD, 23));
        welcomeLabel.setForeground(DARK_GREEN);

        JLabel caption = new JLabel("Your glass, your style. Find the right glass for every space.");
        caption.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        caption.setForeground(MUTED);

        heading.add(welcomeLabel);
        heading.add(Box.createVerticalStrut(4));
        heading.add(caption);

        JLabel tag = new JLabel("CUSTOMER PORTAL");
        tag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tag.setForeground(GREEN);
        tag.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 220, 204), 1, true),
                new EmptyBorder(8, 12, 8, 12)));

        JPanel topActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topActions.setOpaque(false);
        cartTopButton = new JButton("My cart  ·  0");
        cartTopButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        cartTopButton.setForeground(DARK_GREEN);
        cartTopButton.setBackground(new Color(247, 240, 226));
        cartTopButton.setBorder(new CompoundBorder(new LineBorder(new Color(220, 207, 179), 1, true), new EmptyBorder(9, 12, 9, 12)));
        cartTopButton.setFocusPainted(false);
        cartTopButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cartTopButton.addActionListener(e -> showPage("cart"));
        topActions.add(cartTopButton);
        topActions.add(tag);
        top.add(heading, BorderLayout.WEST);
        top.add(topActions, BorderLayout.EAST);

        contentPanel.setBackground(CREAM);
        contentPanel.add(createDashboardPage(), "dashboard");
        contentPanel.add(createProductsPage(), "products");
        contentPanel.add(createCartPage(), "cart");
        contentPanel.add(createOrdersPage(), "orders");
        contentPanel.add(createTrackingPage(), "tracking");
        contentPanel.add(createProfilePage(), "profile");

        main.add(top, BorderLayout.NORTH);
        main.add(contentPanel, BorderLayout.CENTER);
        return main;
    }

    private JPanel pagePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(CREAM);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));
        return panel;
    }

    private JLabel sectionTitle(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Georgia", Font.BOLD, 24));
        label.setForeground(DARK_GREEN);
        return label;
    }

    private void showPage(String page) {
        cardLayout.show(contentPanel, page);
        if ("products".equals(page)) loadProducts();
        if ("cart".equals(page)) refreshCart();
        if ("orders".equals(page)) refreshOrders();
        if ("tracking".equals(page)) refreshTracking();
        if ("profile".equals(page)) loadProfile();
    }

    private JPanel createDashboardPage() {
        JPanel page = pagePanel();
        JPanel hero = new JPanel(new BorderLayout(15, 10));
        hero.setBackground(GREEN);
        hero.setBorder(new EmptyBorder(30, 27, 30, 27));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Order the perfect glass for your space");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Georgia", Font.BOLD, 27));

        JLabel desc = new JLabel("Explore our collection and order glass made for your needs.");
        desc.setForeground(new Color(232, 238, 228));
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton order = actionButton("EXPLORE GLASS  →");
        order.addActionListener(e -> showPage("products"));

        text.add(title);
        text.add(Box.createVerticalStrut(10));
        text.add(desc);
        text.add(Box.createVerticalStrut(20));
        text.add(order);

        JLabel mark = new JLabel("◇");
        mark.setForeground(new Color(222, 207, 166));
        mark.setFont(new Font("Georgia", Font.PLAIN, 90));

        hero.add(text, BorderLayout.CENTER);
        hero.add(mark, BorderLayout.EAST);

        JPanel quick = new JPanel(new GridLayout(1, 3, 15, 0));
        quick.setOpaque(false);
        quick.add(infoCard("GLASS COLLECTION", "Browse our glass products", "◇", "products"));
        quick.add(infoCard("MY CART", "Review selected products", "▣", "cart"));
        quick.add(infoCard("MY ORDERS", "Check your order history", "▤", "orders"));

        JPanel center = new JPanel(new BorderLayout(0, 25));
        center.setOpaque(false);
        center.add(quick, BorderLayout.NORTH);

        JLabel note = new JLabel("<html><div style='width:700px'>Choose your preferred glass, enter your required dimensions and place your order easily. Track your orders through your customer portal.</div></html>");
        note.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        note.setForeground(MUTED);
        center.add(note, BorderLayout.CENTER);

        page.add(hero, BorderLayout.NORTH);
        page.add(center, BorderLayout.CENTER);
        return page;
    }

    private JPanel infoCard(String title, String description, String symbol, String page) {
        JPanel card = new JPanel();
        card.setBackground(IVORY);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 225, 211), 1, true),
                new EmptyBorder(20, 17, 20, 17)));

        JLabel icon = new JLabel(symbol);
        icon.setForeground(GOLD);
        icon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 28));
        JLabel name = new JLabel(title);
        name.setForeground(DARK_GREEN);
        name.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel detail = new JLabel(description);
        detail.setForeground(MUTED);
        detail.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        card.add(icon);
        card.add(Box.createVerticalStrut(10));
        card.add(name);
        card.add(Box.createVerticalStrut(6));
        card.add(detail);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { showPage(page); }
        });
        return card;
    }

    private JPanel createProductsPage() {
        JPanel page = pagePanel();
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(sectionTitle("Order Your Glass"));

        JLabel desc = new JLabel("Choose a glass type and add your required dimensions.");
        desc.setForeground(MUTED);
        desc.setBorder(new EmptyBorder(4, 0, 12, 0));
        heading.add(desc);

        JPanel browseBar = new JPanel(new BorderLayout(10, 0));
        browseBar.setOpaque(false);
        productSearch = new JTextField();
        productSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productSearch.setToolTipText("Search by glass name or description");
        productSearch.setBorder(new CompoundBorder(new LineBorder(new Color(215, 213, 201), 1, true), new EmptyBorder(10, 12, 10, 12)));
        categoryFilter = new JComboBox<>(new String[]{"All glass", "Safety & security", "Privacy", "Tinted", "Decorative"});
        categoryFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoryFilter.setBackground(IVORY);
        categoryFilter.setPreferredSize(new Dimension(190, 40));
        browseBar.add(productSearch, BorderLayout.CENTER);
        browseBar.add(categoryFilter, BorderLayout.EAST);
        browseBar.setBorder(new EmptyBorder(0, 0, 13, 0));
        heading.add(browseBar);
        productSearch.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { loadProducts(); }
        });
        categoryFilter.addActionListener(e -> loadProducts());

        productGrid = new JPanel(new GridLayout(0, 3, 16, 16));
        productGrid.setBackground(CREAM);

        JScrollPane scroll = new JScrollPane(productGrid);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CREAM);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        page.add(heading, BorderLayout.NORTH);
        page.add(scroll, BorderLayout.CENTER);
        return page;
    }

    private void loadProducts() {
        productGrid.removeAll();
        productGrid.setLayout(new GridLayout(0, 3, 16, 16));

        String sql = "SELECT PRODUCT_ID, PRODUCT_NAME, DESCRIPTION, PRICE, STOCK_QTY, IMAGE_PATH, CATEGORY " +
                     "FROM PRODUCTS WHERE ACTIVE_FLAG = 'Y' ORDER BY PRODUCT_ID";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("PRODUCT_NAME");
                String description = rs.getString("DESCRIPTION");
                String category = rs.getString("CATEGORY");
                if (!matchesProductFilter(name, description, category)) continue;
                productGrid.add(productCard(
                        rs.getInt("PRODUCT_ID"),
                        name,
                        description,
                        rs.getDouble("PRICE"),
                        rs.getInt("STOCK_QTY"),
                        rs.getString("IMAGE_PATH")));
            }

            if (productGrid.getComponentCount() == 0) {
                JLabel empty = new JLabel("No glass products match your search. Try another name or category.");
                empty.setForeground(MUTED);
                empty.setBorder(new EmptyBorder(24, 8, 24, 8));
                productGrid.add(empty);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            productGrid.add(new JLabel("Unable to load products: " + ex.getMessage()));
        }

        productGrid.revalidate();
        productGrid.repaint();
    }

    private boolean matchesProductFilter(String name, String description, String category) {
        String n = name == null ? "" : name.toLowerCase();
        String d = description == null ? "" : description.toLowerCase();
        String c = category == null ? "" : category.toLowerCase();
        String query = productSearch == null ? "" : productSearch.getText().trim().toLowerCase();
        if (!query.isEmpty() && !n.contains(query) && !d.contains(query) && !c.contains(query)) return false;
        String selected = categoryFilter == null ? "All glass" : String.valueOf(categoryFilter.getSelectedItem());
        if ("Safety & security".equals(selected)) return n.contains("toughened") || n.contains("tempered") || n.contains("laminated") || n.contains("wired");
        if ("Privacy".equals(selected)) return n.contains("frosted") || c.contains("privacy");
        if ("Tinted".equals(selected)) return n.contains("tinted") || n.contains("reflective") || c.contains("tinted");
        if ("Decorative".equals(selected)) return n.contains("pattern") || n.contains("etched") || c.contains("decorative");
        return true;
    }

    private JPanel productCard(int id, String name, String description,
                               double price, int stock, String imagePath) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(IVORY);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 222, 208), 1, true),
                new EmptyBorder(13, 13, 13, 13)));

        JLabel image = new JLabel();
        image.setHorizontalAlignment(SwingConstants.CENTER);
        image.setPreferredSize(new Dimension(200, 145));
        image.setOpaque(true);
        image.setBackground(new Color(237, 239, 229));
        setProductImage(image, imagePath, name);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel productName = new JLabel(name);
        productName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        productName.setForeground(DARK_GREEN);

        String suitableFor = suitableFor(name);
        JLabel detail = new JLabel("<html><div style='width:230px'>" +
                (description == null || description.trim().isEmpty()
                        ? "Quality glass for your requirements." : description) +
                "<br><br><b>Suitable for:</b> " + suitableFor +
                "</div></html>");
        detail.setForeground(MUTED);
        detail.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JLabel priceLabel = new JLabel("₹ " + money.format(price) + " / sq.ft.");
        priceLabel.setForeground(GREEN);

        JLabel stockLabel = new JLabel("Available stock: " + stock);
        stockLabel.setForeground(MUTED);

        JButton add = actionButton("ADD TO CART");
        add.setEnabled(stock > 0);
        add.addActionListener(e -> addToCart(id, name, price, stock));

        JButton view = new JButton("View Details");
        view.setFont(new Font("Segoe UI", Font.BOLD, 11));
        view.setForeground(DARK_GREEN);
        view.setBackground(IVORY);
        view.setBorder(new CompoundBorder(new LineBorder(new Color(210, 220, 204), 1, true), new EmptyBorder(8, 12, 8, 12)));
        view.setFocusPainted(false);
        view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        view.addActionListener(e -> showProductDetails(name, description, price, stock));

        info.add(productName);
        info.add(Box.createVerticalStrut(7));
        info.add(detail);
        info.add(Box.createVerticalStrut(7));
        info.add(priceLabel);
        info.add(Box.createVerticalStrut(5));
        info.add(stockLabel);
        info.add(Box.createVerticalStrut(10));
        info.add(add);
        info.add(view);

        card.add(image, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    private String suitableFor(String productName) {
        String n = productName == null ? "" : productName.toLowerCase();
        if (n.contains("clear") || n.contains("float"))
            return "windows, doors, partitions and general building use";
        if (n.contains("toughened") || n.contains("tempered"))
            return "shower enclosures, doors, table tops and safety glazing";
        if (n.contains("laminated"))
            return "skylights, shopfronts, balconies and safety applications";
        if (n.contains("frosted"))
            return "bathroom windows, office partitions and privacy areas";
        if (n.contains("tinted"))
            return "facades, windows and spaces needing reduced glare";
        if (n.contains("reflective"))
            return "building exteriors, commercial windows and solar control";
        if (n.contains("wired"))
            return "selected partitions and applications requiring wired glass";
        return "interior design, partitions and suitable architectural applications";
    }

    private void setProductImage(JLabel label, String imagePath, String name) {
        ImageIcon icon = null;

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            java.io.File file = new java.io.File(imagePath);
            if (file.exists()) icon = new ImageIcon(imagePath);

            if (icon == null || icon.getIconWidth() <= 0) {
                java.net.URL url = getClass().getResource(
                        imagePath.startsWith("/") ? imagePath : "/" + imagePath);
                if (url != null) icon = new ImageIcon(url);
            }
        }

        if (icon == null || icon.getIconWidth() <= 0) {
            java.net.URL url = getClass().getResource("/images/" + imageFileFor(name));
            if (url != null) icon = new ImageIcon(url);
        }

        if (icon != null && icon.getIconWidth() > 0) {
            Image scaled = icon.getImage().getScaledInstance(260, 145, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
        } else {
            label.setText("GLASS");
            label.setForeground(GREEN);
            label.setFont(new Font("Georgia", Font.BOLD, 25));
        }
    }

    private String imageFileFor(String name) {
        String n = name == null ? "" : name.toLowerCase();
        if (n.contains("clear")) return "clear_float.jpg.jpeg";
        if (n.contains("tinted")) return "tinted.jpg.jpeg";
        if (n.contains("toughened")) return "toughened.jpg.jpeg";
        if (n.contains("frosted")) return "frosted.jpg.jpeg";
        if (n.contains("laminated")) return "laminated.jpg.jpeg";
        if (n.contains("reflective")) return "reflective.jpg.jpeg";
        if (n.contains("wired")) return "wired.jpg.jpeg";
        return "";
    }

    private void showProductDetails(String name, String description, double price, int stock) {
        JOptionPane.showMessageDialog(this,
                "Glass: " + name +
                "\n\nDescription: " + (description == null ? "Not available" : description) +
                "\n\nSuitable for: " + suitableFor(name) +
                "\n\nPrice: ₹ " + money.format(price) + " / sq.ft." +
                "\nAvailable stock: " + stock,
                "Glass Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addToCart(int productId, String name, double price, int stock) {
        JTextField lengthField = new JTextField();
        JTextField widthField = new JTextField();
        JTextField quantityField = new JTextField("1");

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 12));
        panel.add(new JLabel("Glass:"));
        panel.add(new JLabel(name));
        panel.add(new JLabel("Length (inches):"));
        panel.add(lengthField);
        panel.add(new JLabel("Width (inches):"));
        panel.add(widthField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);

        int choice = JOptionPane.showConfirmDialog(
                this, panel, "Add Glass to Cart", JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) return;

        try {
            double length = Double.parseDouble(lengthField.getText().trim());
            double width = Double.parseDouble(widthField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());

            if (length <= 0 || width <= 0 || quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Dimensions and quantity must be greater than zero.");
                return;
            }

            int alreadyAdded = 0;
            for (CartItem old : cart) {
                if (old.productId == productId) alreadyAdded += old.quantity;
            }
            if (alreadyAdded + quantity > stock) {
                JOptionPane.showMessageDialog(this, "Requested quantity exceeds available stock.");
                return;
            }

            double area = (length * width) / 144.0;
            CartItem item = new CartItem();
            item.productId = productId;
            item.name = name;
            item.length = length;
            item.width = width;
            item.quantity = quantity;
            item.unitPrice = price;
            item.lineTotal = area * price * quantity;
            cart.add(item);
            refreshCartBadge();

            JOptionPane.showMessageDialog(this,
                    name + " added to your cart!\nArea: " + money.format(area) +
                    " sq.ft.\nAmount: ₹ " + money.format(item.lineTotal));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid dimensions and quantity.");
        }
    }

    private JPanel createCartPage() {
        JPanel page = pagePanel();
        page.add(sectionTitle("My Shopping Cart"), BorderLayout.NORTH);

        cartTable = new JTable(new DefaultTableModel(
                new String[]{"Glass", "Length (in)", "Width (in)", "Quantity", "Price / sq.ft.", "Total"}, 0));
        styleTable(cartTable);

        JButton remove = actionButton("Remove Selected");
        JButton checkout = actionButton("PROCEED TO CHECKOUT");

        remove.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row >= 0 && row < cart.size()) {
                cart.remove(row);
                refreshCart();
            } else {
                JOptionPane.showMessageDialog(this, "Select a cart item to remove.");
            }
        });
        checkout.addActionListener(e -> checkoutOrder());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(remove);
        bottom.add(checkout);

        page.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        page.add(bottom, BorderLayout.SOUTH);
        return page;
    }

    private void refreshCart() {
        if (cartTable == null) return;
        DefaultTableModel model = (DefaultTableModel) cartTable.getModel();
        model.setRowCount(0);

        for (CartItem item : cart) {
            model.addRow(new Object[]{
                    item.name, item.length, item.width, item.quantity,
                    "₹ " + money.format(item.unitPrice),
                    "₹ " + money.format(item.lineTotal)
            });
        }
        refreshCartBadge();
    }

    private void refreshCartBadge() {
        if (cartTopButton != null) cartTopButton.setText("My cart  ·  " + cart.size());
    }

    private void checkoutOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty. Please add glass products.");
            return;
        }

        JTextArea addressField = new JTextArea(3, 25);
        addressField.setLineWrap(true);
        addressField.setWrapStyleWord(true);
        double subtotal = calculateSubtotal();
        double gst = subtotal * 0.18;
        double total = subtotal + gst;

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 8));
        panel.add(new JLabel("Delivery Address:"));
        panel.add(new JScrollPane(addressField));
        panel.add(new JLabel("Payment: Pay later (no online payment now)"));
        panel.add(new JLabel("Subtotal: ₹ " + money.format(subtotal)));
        panel.add(new JLabel("GST (18%): ₹ " + money.format(gst)));
        panel.add(new JLabel("Total Amount: ₹ " + money.format(total)));

        int choice = JOptionPane.showConfirmDialog(
                this, panel, "Confirm Your Order", JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) return;

        String address = addressField.getText().trim();
        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your delivery address.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to place this order?\n\n" +
                "Total Amount: ₹ " + money.format(total) +
                "\nPayment: To be completed later",
                "Confirm Order", JOptionPane.YES_NO_OPTION);

        // No online payment gateway for now. A YES confirmation places the order.
        if (confirm == JOptionPane.YES_OPTION) saveOrder(address, "PAY_LATER");
    }

    private double calculateSubtotal() {
        double subtotal = 0;
        for (CartItem item : cart) subtotal += item.lineTotal;
        return subtotal;
    }

    private void saveOrder(String address, String paymentMethod) {
        String orderSql = "INSERT INTO ORDERS " +
                "(ORDER_ID, CUSTOMER_ID, ORDER_DATE, ORDER_TYPE, ORDER_STATUS, " +
                "DELIVERY_ADDRESS, PAYMENT_METHOD, SUBTOTAL, GST_AMOUNT, TOTAL_AMOUNT, " +
                "DELIVERY_STATUS, ADMIN_CONFIRMED, STOCK_RESTORED) " +
                "VALUES (?, ?, SYSTIMESTAMP, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String itemSql = "INSERT INTO ORDER_ITEMS " +
                "(ITEM_ID, ORDER_ID, PRODUCT_ID, ITEM_TYPE, ITEM_DESCRIPTION, " +
                "LENGTH_INCHES, WIDTH_INCHES, QUANTITY, UNIT_PRICE, LINE_TOTAL) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String stockSql = "SELECT STOCK_QTY FROM PRODUCTS " +
                "WHERE PRODUCT_ID = ? AND ACTIVE_FLAG = 'Y' FOR UPDATE";

        try (Connection con = DBConnection.getConnection()) {
            String customerCheck = "SELECT 1 FROM USERS WHERE USER_ID=? AND UPPER(TRIM(ROLE))='CUSTOMER'";
            try (PreparedStatement ps = con.prepareStatement(customerCheck)) {
                ps.setInt(1, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("This customer session is invalid. Please open the login screen and sign in with a registered customer account.");
                    }
                }
            }
            con.setAutoCommit(false);

            try {
                // Lock both order tables while assigning MAX+1 identifiers.
                try (Statement st = con.createStatement()) {
                    st.execute("LOCK TABLE ORDERS IN EXCLUSIVE MODE");
                    st.execute("LOCK TABLE ORDER_ITEMS IN EXCLUSIVE MODE");
                }

                int orderId = getNextId(con, "ORDERS", "ORDER_ID");
                int nextItemId = getNextId(con, "ORDER_ITEMS", "ITEM_ID");

                // Recheck stock immediately before saving the order.
                for (CartItem item : cart) {
                    try (PreparedStatement ps = con.prepareStatement(stockSql)) {
                        ps.setInt(1, item.productId);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next() || rs.getInt("STOCK_QTY") < item.quantity) {
                                throw new SQLException("Insufficient stock for " + item.name);
                            }
                        }
                    }
                }

                double subtotal = calculateSubtotal();
                double gst = subtotal * 0.18;
                double total = subtotal + gst;

                try (PreparedStatement ps = con.prepareStatement(orderSql)) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, customerId);
                    ps.setString(3, "CUSTOM");
                    ps.setString(4, "PENDING");
                    ps.setString(5, address);
                    ps.setString(6, paymentMethod);
                    ps.setDouble(7, subtotal);
                    ps.setDouble(8, gst);
                    ps.setDouble(9, total);
                    ps.setString(10, "PENDING");
                    ps.setString(11, "N");
                    ps.setString(12, "N");
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(itemSql)) {
                    for (CartItem item : cart) {
                        ps.setInt(1, nextItemId++);
                        ps.setInt(2, orderId);
                        ps.setInt(3, item.productId);
                        ps.setString(4, "CUSTOM");
                        ps.setString(5, item.name);
                        ps.setDouble(6, item.length);
                        ps.setDouble(7, item.width);
                        ps.setInt(8, item.quantity);
                        ps.setDouble(9, item.unitPrice);
                        ps.setDouble(10, item.lineTotal);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                String adminSql = "SELECT USER_ID FROM USERS WHERE UPPER(TRIM(ROLE)) IN ('ADMIN','ADMINISTRATOR')";
                try (PreparedStatement admins = con.prepareStatement(adminSql); ResultSet rs = admins.executeQuery()) {
                    while (rs.next()) {
                        try (PreparedStatement notification = con.prepareStatement(
                                "INSERT INTO NOTIFICATIONS(USER_ID,TITLE,MESSAGE,NOTIFICATION_TYPE,IS_READ,CREATED_AT) VALUES(?,?,?,'ORDER','N',SYSTIMESTAMP)")) {
                            notification.setInt(1, rs.getInt(1));
                            notification.setString(2, "New customer order to review");
                            notification.setString(3, "Order #" + orderId + " is awaiting confirmation.");
                            notification.executeUpdate();
                        }
                    }
                }

                con.commit();
                cart.clear();
                refreshCartBadge();

                JOptionPane.showMessageDialog(this,
                        "Your order has been placed successfully!\n\nOrder ID: " + orderId +
                        "\nTotal Amount: ₹ " + money.format(total) +
                        "\nPayment: " + paymentMethod +
                        "\nStatus: PENDING",
                        "Order Successful", JOptionPane.INFORMATION_MESSAGE);

                showPage("orders");

            } catch (Exception ex) {
                con.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Unable to place your order.\n\n" + ex.getMessage(),
                    "Order Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getNextId(Connection con, String table, String column) throws SQLException {
        // Table and column names are fixed constants in the calls above.
        String sql = "SELECT NVL(MAX(" + column + "), 0) + 1 FROM " + table;
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private JPanel createOrdersPage() {
        JPanel page = pagePanel();
        page.add(sectionTitle("My Orders"), BorderLayout.NORTH);

        ordersTable = new JTable(new DefaultTableModel(
                new String[]{"Order ID", "Order Date", "Order Type", "Order Status", "Delivery Status", "Total Amount"}, 0));
        styleTable(ordersTable);

        JButton refresh = actionButton("Refresh Orders");
        JButton cancel = actionButton("Cancel Selected Order");
        refresh.addActionListener(e -> refreshOrders());
        cancel.addActionListener(e -> cancelSelectedOrder());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(cancel);
        bottom.add(refresh);

        page.add(new JScrollPane(ordersTable), BorderLayout.CENTER);
        page.add(bottom, BorderLayout.SOUTH);
        return page;
    }

    private void refreshOrders() {
        if (ordersTable == null) return;
        DefaultTableModel model = (DefaultTableModel) ordersTable.getModel();
        model.setRowCount(0);

        String sql = "SELECT ORDER_ID, ORDER_DATE, ORDER_TYPE, ORDER_STATUS, DELIVERY_STATUS, TOTAL_AMOUNT " +
                     "FROM ORDERS WHERE CUSTOMER_ID = ? ORDER BY ORDER_DATE DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("ORDER_ID"),
                            rs.getTimestamp("ORDER_DATE"),
                            rs.getString("ORDER_TYPE"),
                            rs.getString("ORDER_STATUS"),
                            rs.getString("DELIVERY_STATUS"),
                            "₹ " + money.format(rs.getDouble("TOTAL_AMOUNT"))
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to load orders: " + ex.getMessage());
        }
    }

    private JPanel createTrackingPage() {
        JPanel page = pagePanel();
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel title = new JPanel();
        title.setOpaque(false);
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        title.add(sectionTitle("Track Your Orders"));
        JLabel subtitle = new JLabel("Follow every step, from order confirmation to delivery.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(MUTED);
        subtitle.setBorder(new EmptyBorder(3, 0, 12, 0));
        title.add(subtitle);
        header.add(title, BorderLayout.WEST);
        JButton refresh = actionButton("Refresh Tracking");
        refresh.addActionListener(e -> refreshTracking());
        header.add(refresh, BorderLayout.EAST);
        trackingCards = new JPanel();
        trackingCards.setLayout(new BoxLayout(trackingCards, BoxLayout.Y_AXIS));
        trackingCards.setBackground(CREAM);
        JScrollPane scroll = new JScrollPane(trackingCards);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CREAM);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        page.add(header, BorderLayout.NORTH);
        page.add(scroll, BorderLayout.CENTER);
        return page;
    }

    private void refreshTracking() {
        if (trackingCards == null) return;
        trackingCards.removeAll();
        String sql = "SELECT O.ORDER_ID,O.ORDER_DATE,O.ORDER_STATUS,O.DELIVERY_STATUS, "
                + "(SELECT MAX(I.ITEM_DESCRIPTION) FROM ORDER_ITEMS I WHERE I.ORDER_ID=O.ORDER_ID) ITEM_DESCRIPTION "
                + "FROM ORDERS O WHERE O.CUSTOMER_ID=? ORDER BY O.ORDER_DATE DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    trackingCards.add(createTrackingCard(
                            rs.getInt("ORDER_ID"), rs.getTimestamp("ORDER_DATE"),
                            rs.getString("ITEM_DESCRIPTION"), rs.getString("ORDER_STATUS"),
                            rs.getString("DELIVERY_STATUS")));
                    trackingCards.add(Box.createVerticalStrut(14));
                    count++;
                }
                if (count == 0) {
                    JPanel empty = new JPanel(new BorderLayout(0, 8));
                    empty.setBackground(IVORY);
                    empty.setBorder(new CompoundBorder(new LineBorder(new Color(226, 222, 208), 1, true),
                            new EmptyBorder(28, 24, 28, 24)));
                    JLabel title = new JLabel("No orders yet");
                    title.setFont(new Font("Georgia", Font.BOLD, 20));
                    title.setForeground(DARK_GREEN);
                    JLabel message = new JLabel("Your order updates will appear here after you place an order.");
                    message.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    message.setForeground(MUTED);
                    empty.add(title, BorderLayout.NORTH);
                    empty.add(message, BorderLayout.CENTER);
                    trackingCards.add(empty);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Unable to load tracking details: " + ex.getMessage());
        }
        trackingCards.revalidate();
        trackingCards.repaint();
    }

    private JPanel createTrackingCard(int orderId, Timestamp orderDate, String item, String orderStatus, String deliveryStatus) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(IVORY);
        card.setBorder(new CompoundBorder(new LineBorder(new Color(226, 222, 208), 1, true),
                new EmptyBorder(18, 20, 16, 20)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JPanel top = new JPanel(new BorderLayout(12, 4));
        top.setOpaque(false);
        JPanel orderInfo = new JPanel();
        orderInfo.setOpaque(false);
        orderInfo.setLayout(new BoxLayout(orderInfo, BoxLayout.Y_AXIS));
        JLabel orderLabel = new JLabel("Order #" + orderId);
        orderLabel.setFont(new Font("Georgia", Font.BOLD, 17));
        orderLabel.setForeground(DARK_GREEN);
        JLabel dateLabel = new JLabel(orderDate == null ? "Order date unavailable" :
                "Placed " + new java.text.SimpleDateFormat("dd MMM yyyy · hh:mm a").format(orderDate));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLabel.setForeground(MUTED);
        orderInfo.add(orderLabel);
        orderInfo.add(Box.createVerticalStrut(3));
        orderInfo.add(dateLabel);
        top.add(orderInfo, BorderLayout.WEST);

        String readableStatus = readableOrderStatus(orderStatus, deliveryStatus);
        JLabel badge = new JLabel(readableStatus);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(isClosedStatus(orderStatus) ? new Color(151, 73, 62) : GREEN);
        badge.setOpaque(true);
        badge.setBackground(isClosedStatus(orderStatus) ? new Color(250, 237, 233) : new Color(232, 240, 232));
        badge.setBorder(new EmptyBorder(7, 11, 7, 11));
        top.add(badge, BorderLayout.EAST);
        card.add(top);

        if (item != null && !item.trim().isEmpty()) {
            JLabel product = new JLabel("Product  ·  " + item);
            product.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            product.setForeground(MUTED);
            product.setBorder(new EmptyBorder(9, 0, 3, 0));
            card.add(product);
        } else {
            card.add(Box.createVerticalStrut(12));
        }

        int stage = trackingStage(orderStatus, deliveryStatus);
        JourneyTrack journey = new JourneyTrack(stage, isClosedStatus(orderStatus));
        journey.setAlignmentX(Component.LEFT_ALIGNMENT);
        journey.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        card.add(journey);

        JPanel labels = new JPanel(new GridLayout(1, 7, 2, 0));
        labels.setOpaque(false);
        String[] stages = {"Placed", "Confirmed", "Production", "Quality", "Ready to ship", "In transit", "Delivered"};
        for (int i = 0; i < stages.length; i++) {
            JLabel label = new JLabel("<html><center>" + stages[i] + "</center></html>", SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            label.setForeground(i <= stage && stage >= 0 ? GREEN : MUTED);
            labels.add(label);
        }
        labels.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(labels);
        return card;
    }

    private int trackingStage(String orderStatus, String deliveryStatus) {
        String s = ((orderStatus == null ? "" : orderStatus) + " " + (deliveryStatus == null ? "" : deliveryStatus)).toUpperCase();
        if (s.contains("CANCEL") || s.contains("REJECT")) return -1;
        if (s.contains("DELIVERED") || s.contains("COMPLETED")) return 6;
        if (s.contains("IN_TRANSIT") || s.contains("TRANSIT")) return 5;
        if (s.contains("READY_FOR_DISPATCH") || s.contains("INSPECTION_PASSED") || s.contains("PENDING_APPROVAL")) return 4;
        if (s.contains("INSPECTION") || s.contains("QUALITY")) return 3;
        if (s.contains("PRODUCTION") || s.contains("PROGRESS")) return 2;
        if (s.contains("CONFIRM")) return 1;
        return 0;
    }

    private boolean isClosedStatus(String status) {
        String s = status == null ? "" : status.toUpperCase();
        return s.contains("CANCEL") || s.contains("REJECT");
    }

    private String readableOrderStatus(String orderStatus, String deliveryStatus) {
        int stage = trackingStage(orderStatus, deliveryStatus);
        if (stage < 0) return orderStatus != null && orderStatus.toUpperCase().contains("REJECT") ? "Order rejected" : "Order cancelled";
        String[] labels = {"Order received", "Confirmed", "In production", "Quality check", "Ready to dispatch", "In transit", "Delivered"};
        return labels[stage];
    }

    private static class JourneyTrack extends JPanel {
        private final int stage;
        private final boolean closed;
        JourneyTrack(int stage, boolean closed) {
            this.stage = stage;
            this.closed = closed;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int left = 18, right = Math.max(left + 1, getWidth() - 18), y = 17;
            int count = 7, gap = (right - left) / (count - 1);
            g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(226, 230, 222));
            g.drawLine(left, y, right, y);
            int completed = Math.max(0, Math.min(stage, count - 1));
            if (!closed && stage >= 0) {
                g.setColor(GOLD);
                g.drawLine(left, y, left + gap * completed, y);
            }
            for (int i = 0; i < count; i++) {
                int x = left + gap * i;
                g.setColor(!closed && stage >= i ? GREEN : Color.WHITE);
                g.fillOval(x - 5, y - 5, 10, 10);
                g.setColor(!closed && stage >= i ? GREEN : new Color(205, 211, 202));
                g.setStroke(new BasicStroke(1.5f));
                g.drawOval(x - 5, y - 5, 10, 10);
            }
            if (!closed && stage >= 0) {
                int x = left + gap * completed;
                g.setColor(DARK_GREEN);
                g.fillRoundRect(x - 11, 1, 17, 9, 3, 3);
                g.fillRect(x + 4, 4, 7, 6);
                g.setColor(new Color(238, 225, 190));
                g.fillRect(x + 6, 5, 3, 3);
                g.setColor(GOLD);
                g.fillOval(x - 8, 8, 5, 5);
                g.fillOval(x + 5, 8, 5, 5);
            }
            g.dispose();
        }
    }

    private void cancelSelectedOrder() {
        if (ordersTable == null || ordersTable.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Select an order first."); return;
        }
        int row = ordersTable.convertRowIndexToModel(ordersTable.getSelectedRow());
        int orderId = Integer.parseInt(ordersTable.getModel().getValueAt(row, 0).toString());
        String status = String.valueOf(ordersTable.getModel().getValueAt(row, 3));
        if (!"PENDING".equalsIgnoreCase(status) && !"PENDING_APPROVAL".equalsIgnoreCase(status)
                && !"CONFIRMED".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "An order can only be cancelled before manufacturing starts."); return;
        }
        if (JOptionPane.showConfirmDialog(this, "Cancel Order #" + orderId + "?", "Cancel Order", JOptionPane.YES_NO_OPTION)
                != JOptionPane.YES_OPTION) return;
        String update = "UPDATE ORDERS SET ORDER_STATUS='CANCELLED',DELIVERY_STATUS='CANCELLED' "
                + "WHERE ORDER_ID=? AND CUSTOMER_ID=? AND ORDER_STATUS IN ('PENDING','PENDING_APPROVAL','CONFIRMED')";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(update)) {
                ps.setInt(1, orderId); ps.setInt(2, customerId);
                if (ps.executeUpdate() == 0) throw new SQLException("Order status changed. Refresh and try again.");
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE EMPLOYEE_TASKS SET TASK_STATUS='CANCELLED',UPDATED_AT=SYSTIMESTAMP WHERE ORDER_ID=? AND TASK_STATUS='ASSIGNED'")) {
                ps.setInt(1, orderId); ps.executeUpdate();
            }
            String admins = "SELECT USER_ID FROM USERS WHERE UPPER(TRIM(ROLE)) IN ('ADMIN','ADMINISTRATOR')";
            try (PreparedStatement ap = con.prepareStatement(admins); ResultSet rs = ap.executeQuery()) {
                while (rs.next()) {
                    try (PreparedStatement np = con.prepareStatement("INSERT INTO NOTIFICATIONS(USER_ID,TITLE,MESSAGE,NOTIFICATION_TYPE,IS_READ,CREATED_AT) VALUES(?,?,?,'ORDER','N',SYSTIMESTAMP)")) {
                        np.setInt(1, rs.getInt(1)); np.setString(2, "Customer cancelled order");
                        np.setString(3, "Order #" + orderId + " was cancelled by the customer."); np.executeUpdate();
                    }
                }
            }
            con.commit();
            JOptionPane.showMessageDialog(this, "Order cancelled. The status is updated.");
            refreshOrders(); refreshTracking();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Unable to cancel order: " + ex.getMessage());
        }
    }

    private JPanel createProfilePage() {
        JPanel page = pagePanel();
        page.add(sectionTitle("My Profile"), BorderLayout.NORTH);

        JPanel profile = new JPanel(new BorderLayout(0, 20));
        profile.setBackground(IVORY);
        profile.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 222, 208), 1, true),
                new EmptyBorder(30, 35, 30, 35)));

        profileDetailsLabel = new JLabel("Loading profile...");
        profileDetailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        profileDetailsLabel.setForeground(DARK_GREEN);
        profileDetailsLabel.setVerticalAlignment(SwingConstants.TOP);

        JButton refresh = actionButton("Refresh Profile");
        refresh.addActionListener(e -> loadProfile());

        profile.add(profileDetailsLabel, BorderLayout.CENTER);
        profile.add(refresh, BorderLayout.SOUTH);

        page.add(profile, BorderLayout.CENTER);
        return page;
    }

    private void loadCustomerName() {
        String sql = "SELECT FULL_NAME FROM USERS WHERE USER_ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) welcomeLabel.setText("Welcome, " + rs.getString("FULL_NAME"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadProfile() {
        if (profileDetailsLabel == null) return;

        profileDetailsLabel.setText("Loading profile...");

        String sql = "SELECT USER_ID, FULL_NAME, USERNAME, ROLE, PHONE, EMAIL, " +
                     "GENDER, ADDRESS, DOB, CREATED_AT FROM USERS WHERE USER_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.sql.Date dob = rs.getDate("DOB");
                    Timestamp createdAt = rs.getTimestamp("CREATED_AT");
                    String details = "<html><div style='font-family:SansSerif;'>" +
                            "<h2>My Account Details</h2>" +
                            "<p><b>User ID:</b> " + rs.getInt("USER_ID") + "</p>" +
                            "<p><b>Full Name:</b> " + safe(rs.getString("FULL_NAME")) + "</p>" +
                            "<p><b>Username:</b> " + safe(rs.getString("USERNAME")) + "</p>" +
                            "<p><b>Role:</b> " + safe(rs.getString("ROLE")) + "</p>" +
                            "<p><b>Email:</b> " + safe(rs.getString("EMAIL")) + "</p>" +
                            "<p><b>Phone:</b> " + safe(rs.getString("PHONE")) + "</p>" +
                            "<p><b>Gender:</b> " + safe(rs.getString("GENDER")) + "</p>" +
                            "<p><b>Address:</b> " + safe(rs.getString("ADDRESS")) + "</p>" +
                            "<p><b>Date of Birth:</b> " +
                            (dob == null ? "Not provided" : new java.text.SimpleDateFormat("dd-MM-yyyy").format(dob)) + "</p>" +
                            "<p><b>Account Created:</b> " +
                            (createdAt == null ? "Not available" : new java.text.SimpleDateFormat("dd-MM-yyyy hh:mm a").format(createdAt)) +
                            "</p></div></html>";
                    profileDetailsLabel.setText(details);
                } else {
                    profileDetailsLabel.setText("<html><b>Profile not found</b><br><br>" +
                            "No account was found for User ID: " + customerId + "</html>");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            profileDetailsLabel.setText("<html><b>Unable to load profile.</b><br><br>" +
                    safe(ex.getMessage()) + "</html>");
        }
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "Not provided" : value;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setBackground(IVORY);
        table.setForeground(DARK_GREEN);
        table.setGridColor(new Color(225, 220, 205));
        table.getTableHeader().setBackground(GREEN);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionBackground(new Color(215, 229, 215));
        table.setSelectionForeground(DARK_GREEN);
    }

    private JButton actionButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(GREEN);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 16, 11, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static class CartItem {
        int productId;
        String name;
        double length;
        double width;
        int quantity;
        double unitPrice;
        double lineTotal;
    }

    public static void main(String[] args) {
        // Do not use a hard-coded customer ID: it may not exist or may belong to an admin.
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
