package org.example.httpRequests

object PreparedConversations {

    val semanticDivMessages = listOf(
        Message(
            role = "system",
            content = "You are an expert in web accessibility and semantic HTML. Your task is to analyze `<div>` elements and rate their semantic level based on the following rules:\n\n**Rating Scale**:\n- **Low**: No semantic meaning (e.g., random-looking class or ID names, framework wrappers).\n- **Medium**: Some semantic hints (e.g., partially meaningful ID or test attribute).\n- **High**: Clear semantic intent (e.g., `role`, `aria-*`, or descriptive `data-testid`).\n\n IMPORTANT:\n- Repeated, clustered, or capitalized class names like `AbFgH`, `RtYo2p`, or `jtoKL` are still considered **low** unless there's a descriptive `data-testid` or ARIA.\n- DO NOT assume semantic meaning from repetition or quantity of classes.\n\nStick to this **exact format**:\n\n1. `<div ...>` // [rating] – [brief explanation]"
        ),
        Message(
            role = "user",
            content = "Rate these <div> elements:\n1. <div class=\"a1b2c3\">\n2. <div class=\"XyZ123\">\n3. <div class=\"jkLp09\">\n4. <div class=\"fooBar123\">"
        ),
        Message(
            role = "assistant",
            content = "1. <div class=\"a1b2c3\"> // low – random alphanumeric class name with no semantic value.\n2. <div class=\"XyZ123\"> // low – non-descriptive and likely auto-generated.\n3. <div class=\"jkLp09\"> // low – meaningless string, no semantic indication.\n4. <div class=\"fooBar123\"> // low – pseudo-random, lacks meaningful context."
        ),
        Message(
            role = "user",
            content = "Rate these <div> elements:\n1. <div data-testid=\"login-form\">\n2. <div id=\"header-container\">\n3. <div id=\"__next\">\n4. <div data-testid=\"menu-wrapper\" class=\"xyz123\">"
        ),
        Message(
            role = "assistant",
            content = "1. <div data-testid=\"login-form\"> // high – clearly indicates a login form element.\n2. <div id=\"header-container\"> // medium – name suggests purpose but lacks ARIA or role.\n3. <div id=\"__next\"> // low – framework wrapper, no direct semantic role.\n4. <div data-testid=\"menu-wrapper\" class=\"xyz123\"> // high – descriptive `data-testid` shows component's purpose."
        ),
        Message(
            role = "user",
            content = "Rate these <div> elements:\n1. <div role=\"navigation\">\n2. <div role=\"button\" tabindex=\"0\">\n3. <div aria-label=\"Search results\" role=\"region\">\n4. <div role=\"combobox\" aria-controls=\"suggestions\">"
        ),
        Message(
            role = "assistant",
            content = "1. <div role=\"navigation\"> // high – explicit role defining navigation.\n2. <div role=\"button\" tabindex=\"0\"> // medium – suggests interactivity but lacks specific context.\n3. <div aria-label=\"Search results\" role=\"region\"> // high – labeled region, meaningful for accessibility.\n4. <div role=\"combobox\" aria-controls=\"suggestions\"> // high – ARIA role adds rich semantic detail."
        ),
        Message(
            role = "user",
            content = "Rate these <div> elements:\n1. <div class=\"abcXYZ QwErTy zZzZzZ\">\n2. <div id=\"footer-section\">\n3. <div class=\"lkJH98 DfGhT5\" data-testid=\"product-tile\">"
        ),
        Message(
            role = "assistant",
            content = "1. <div class=\"abcXYZ QwErTy zZzZzZ\"> // low – alphanumeric class names, likely used only for styling.\n2. <div class=\"abcXYZ T11Ed6 QwErTy AyOlkl\"> // low – purely presentational with no semantic clues.\n3. <div class=\"vzC9TR abcXYZ REZdEJ\" data-testid=\"search-form\"> // high – data-testid indicates a search form, essential for functionality."
        )
    )
}