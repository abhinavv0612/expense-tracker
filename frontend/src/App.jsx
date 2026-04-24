import { useState, useEffect } from "react";
import "./App.css"

function App() {
    const [form, setForm] = useState({
        amount: "",
        category: "",
        description: "",
        date: "",
    });

    const [expenses, setExpenses] = useState([]);
    const [total, setTotal] = useState(0);
    const [categoryFilter, setCategoryFilter] = useState("");
    const [categories, setCategories] = useState([]);
    const [sortOrder, setSortOrder] = useState("desc");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!form.amount || !form.category || !form.date) {
            alert("Please fill all required fields");
            return;
        }

        if (parseFloat(form.amount) <= 0) {
            alert("Amount must be greater than 0");
            return;
        }

        try {
            setLoading(true);
            setError("");

            const response = await fetch("http://localhost:8080/expenses", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Idempotency-Key": Date.now().toString(),
                },
                body: JSON.stringify({
                    amount: form.amount ? parseFloat(form.amount) : 0,
                    category: form.category,
                    description: form.description,
                    date: form.date,
                }),
            });

            if (!response.ok) {
                throw new Error("Failed to save expense");
            }

            const data = await response.json();
            console.log("Saved:", data);
            setError("");
            setSuccess("Expense added successfully!");
            setTimeout(() => setSuccess(""), 2000);

            await fetchExpenses();

            // reset form
            setForm({
                amount: "",
                category: "",
                description: "",
                date: "",
            });

        } catch (error) {
            console.error(error);
            setError("Failed to add expense");
        } finally {
            setLoading(false);
        }
    };

    const fetchExpenses = async () => {
        try {
            let url = "http://localhost:8080/expenses";

            const params = [];

            if (categoryFilter) {
                params.push(`category=${categoryFilter}`);
            }

            if (sortOrder === "desc") {
                params.push("sort=date_desc");
            } else {
                params.push("sort=date_asc");
            }

            if (params.length > 0) {
                url += "?" + params.join("&");
            }

            const res = await fetch(url);
            const data = await res.json();

            setExpenses(data.expenses);
            const uniqueCategories = [
                ...new Set(data.expenses.map((e) => e.category)),
            ];

            setCategories(uniqueCategories);
            setTotal(data.total);

        } catch (err) {
            console.error("Error fetching expenses", err);
        }
    };

    useEffect(() => {
        fetchExpenses();
    }, [categoryFilter, sortOrder]);

    const deleteExpense = async (id) => {
        try {
            await fetch(`http://localhost:8080/expenses/${id}`, {
                method: "DELETE",
            });

            // refresh list
            fetchExpenses();

        } catch (err) {
            console.error("Delete failed", err);
        }
    };

    return (
        <div className="container">
            <h1>Expense Tracker</h1>

            {/* ADD EXPENSE CARD */}
            <div className="card">
                <h2>Add Expense</h2>

                {error && <p style={{ color: "red" }}>{error}</p>}
                {success && <p style={{ color: "green" }}>{success}</p>}
                <form onSubmit={handleSubmit} className = "form">
                    <input
                        type="number"
                        placeholder="Amount"
                        value={form.amount}
                        onChange={(e) =>
                            setForm({ ...form, amount: e.target.value })
                        }
                    />

                    <input
                        type="text"
                        placeholder="Category"
                        value={form.category}
                        onChange={(e) =>
                            setForm({ ...form, category: e.target.value })
                        }
                    />

                    <input
                        type="text"
                        placeholder="Description"
                        value={form.description}
                        onChange={(e) =>
                            setForm({ ...form, description: e.target.value })
                        }
                    />

                    <input
                        type="date"
                        value={form.date}
                        onChange={(e) =>
                            setForm({ ...form, date: e.target.value })
                        }
                    />

                    <button type="submit" disabled={loading}>
                        {loading ? "Adding..." : "Add Expense"}
                    </button>
                </form>
            </div>

            {/* FILTER + SORT */}
            <div className="card">
                <div className="filter-sort">
                    <select
                        value={categoryFilter}
                        onChange={(e) => setCategoryFilter(e.target.value)}
                    >
                        <option value="">All Categories</option>

                        {categories.map((cat) => (
                            <option key={cat} value={cat}>
                                {cat}
                            </option>
                        ))}
                    </select>

                    <select
                        value={sortOrder}
                        onChange={(e) => setSortOrder(e.target.value)}
                    >
                        <option value="desc">Newest First</option>
                        <option value="asc">Oldest First</option>
                    </select>
                </div>
            </div>

            {/* EXPENSE LIST */}
            <div className="card">
                <h2>All Expenses</h2>

                {expenses.length === 0 ? (
                    <p style={{ opacity: 0.7 }}>No expenses yet</p>
                ) : (
                    <ul>
                        {expenses.map((exp) => (
                            <li key={exp.id}>
                                <span className="amount">₹{exp.amount}</span>
                                {" | "}
                                <span style={{ color: "#93c5fd" }}>{exp.category}</span>
                                {" | "}
                                {exp.description}
                                {" | "}
                                {new Date(exp.date).toLocaleDateString()}

                                {"  "}
                                <button
                                    style={{
                                        marginLeft: "10px",
                                        background: "#ef4444",
                                        padding: "4px 8px",
                                        fontSize: "12px",
                                    }}
                                    onClick={() => deleteExpense(exp.id)}
                                >
                                    Delete
                                </button>
                            </li>
                        ))}
                    </ul>
                )}

                <div className="total">Total: ₹{total}</div>
            </div>
        </div>
    );
}

export default App;