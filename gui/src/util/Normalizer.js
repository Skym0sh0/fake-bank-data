function normalizeTransaction(t) {
    return {
        id: t.id,
        date: t.date,
        amountInCents: t.amount,
        isPeriodic: t.isPeriodic,
        reasons: [t.reason],
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
        balanceInCents: parseInt(stmt.balance),
        transactions: transactions,
        previousStatementId: (stmt.previousStatement && stmt.previousStatement.id) || null
    }
}

function denormalizeStatement(stmt) {
    return {
        id: stmt.id,
        date: stmt.date,
        balance: stmt.balanceInCents,
        transactions: stmt.transactions,
        previousStatement: stmt.previousStatement,
    }
}

export {
    normalizeTransaction,
    denormalizeTransaction,
    normalizeStatement,
    denormalizeStatement,
}
