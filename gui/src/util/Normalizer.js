function normalizeTransaction(t) {
    return {
        id: t.id,
        date: t.date,
        amount: t.amount,
        isPeriodic: t.isPeriodic,
        reason: t.reason,
    }
}

function denormalizeTransaction(t) {
    return t
}

function normalizeStatement(stmt) {
    const transactions = (stmt.transactions || []).map(t => normalizeTransaction(t))

    return {
        id: stmt.id,
        date: stmt.date,
        balance: parseInt(stmt.balance),
        transactions: transactions,
        previousStatementId: (stmt.previousStatement && stmt.previousStatement.id) || null
    }
}

function denormalizeStatement(stmt) {
    return stmt
}

export {
    normalizeTransaction,
    denormalizeTransaction,
    normalizeStatement,
    denormalizeStatement,
}
