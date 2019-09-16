function normalizeTransaction(t) {
    return {
        id: t.id,
        date: t.date,
        amount: t.amount,
        isPeriodic: t.isPeriodic,
        reason: t.reason,
    }
}

function normalizeStatement(stmt) {
    const transactions = (stmt.transactions || []).map(t => normalizeTransaction(t))

    return {
        id: stmt.id,
        date: stmt.date,
        balance: parseInt(stmt.balance),
        transactions: transactions,
    }
}

export {
    normalizeStatement
}
