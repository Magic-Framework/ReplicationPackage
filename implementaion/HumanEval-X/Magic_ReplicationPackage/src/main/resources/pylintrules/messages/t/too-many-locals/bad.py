from childhood import Child, Sweet


def handle_sweets(infos):  # [too-many-locals]
    # Create children
    children = [Child(info) for info in infos]
    number_of_sweets = 87
    sweets = [Sweet() * number_of_sweets]
    number_of_sweet_per_child = 5
    money = 45.0
    sweets_given = 0
    time_to_eat_sweet = 54
    price_of_sweet = 0.42
    # distribute sweet
    for child in children:
        sweets_given += number_of_sweet_per_child
        child.give(sweets[number_of_sweet_per_child:])
    # calculate prices
    cost = sweets_given * price_of_sweet
    cost_of_children = cost
    # Calculate remaining money
    remaining = money - cost_of_children
    remaining_money = remaining
    # Calculate time it took
    time_it_took_assuming = time_to_eat_sweet * number_of_sweet_per_child
    time_it_took_assuming_parallel_eating = (
        time_it_took_assuming
    )
    print(
        f"{children} ate {cost_of_children}¤ of sweets in {time_it_took_assuming_parallel_eating}, "
        f"you still have {remaining_money}"
    )
