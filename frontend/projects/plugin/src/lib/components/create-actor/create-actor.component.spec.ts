import {ComponentFixture, TestBed} from '@angular/core/testing';
import {of, Subject} from 'rxjs';

import {CreateActorComponent} from './create-actor.component';
import {CreateActorConfig} from '../../models/create-actor-config';

describe('CreateActorComponent', () => {
    let component: CreateActorComponent;
    let fixture: ComponentFixture<CreateActorComponent>;
    let save$: Subject<void>;

    const emptyConfig = {
        naam: '',
        soortActor: '',
        resultPvName: '',
    } satisfies Partial<CreateActorConfig>;
    const validConfig = {...emptyConfig, naam: 'x', soortActor: 'x', resultPvName: 'x'} satisfies Partial<CreateActorConfig>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [CreateActorComponent]
        })
            .overrideTemplate(CreateActorComponent, '')
            .compileComponents();

        save$ = new Subject<void>();

        fixture = TestBed.createComponent(CreateActorComponent);
        component = fixture.componentInstance;

        component.save$ = save$;
        component.disabled$ = of(false);
        component.pluginId = 'openklant';
        component.prefillConfiguration$ = of(emptyConfig as CreateActorConfig);

        fixture.detectChanges();
    });

    afterEach(() => save$.complete());

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should report an incomplete configuration as invalid', () => {
        const emitted: boolean[] = [];
        component.valid.subscribe(valid => emitted.push(valid));

        component.formValueChange(emptyConfig);

        expect(emitted).toEqual([false]);
    });

    it('should report a configuration with every required field as valid', () => {
        const emitted: boolean[] = [];
        component.valid.subscribe(valid => emitted.push(valid));

        component.formValueChange(validConfig);

        expect(emitted).toEqual([true]);
    });

    it('should emit the configuration on save when it is valid', () => {
        const configurations: unknown[] = [];
        component.configuration.subscribe(configuration => configurations.push(configuration));

        component.formValueChange(validConfig);
        save$.next();

        expect(configurations).toEqual([validConfig]);
    });

    it('should not emit the configuration on save when it is invalid', () => {
        const configurations: unknown[] = [];
        component.configuration.subscribe(configuration => configurations.push(configuration));

        component.formValueChange(emptyConfig);
        save$.next();

        expect(configurations).toEqual([]);
    });
});
