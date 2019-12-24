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
    return {
        id: t.id,
        date: t.date,
        amount: t.amountInCents,
        isPeriodic: t.isPeriodic,
        reason: t.reasons[0],
    }
}

function normalizeStatement(stmt) {
    const transactions = (stmt.transactions || []).map(t => normalizeTransaction(t))

    return {
        id: stmt.id,
        date: stmt.date,
        finalBalanceInCents: parseInt(stmt.balance),
        transactions: transactions,
        previousStatementId: (stmt.previousStatement && stmt.previousStatement.id) || null
    }
}

function denormalizeStatement(stmt) {
    return {
        id: stmt.id,
        date: stmt.date,
        balance: stmt.finalBalanceInCents,
        transactions: stmt.transactions && stmt.transactions.map(t => denormalizeTransaction(t)),
        previousStatement: stmt.previousStatement,
    }
}

export {
    normalizeTransaction,
    denormalizeTransaction,
    normalizeStatement,
    denormalizeStatement,
}
