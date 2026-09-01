import {ComponentFixture, TestBed} from '@angular/core/testing';
import {of, Subject} from 'rxjs';

import {UpdateBetrokkeneComponent} from './update-betrokkene.component';
import {UpdateBetrokkeneConfig} from '../../models/update-betrokkene-config';

describe('UpdateBetrokkeneComponent', () => {
    let component: UpdateBetrokkeneComponent;
    let fixture: ComponentFixture<UpdateBetrokkeneComponent>;
    let save$: Subject<void>;

    const emptyConfig = {
        uuid: '',
        resultPvName: '',
    } satisfies Partial<UpdateBetrokkeneConfig>;
    const validConfig = {...emptyConfig, uuid: 'x', resultPvName: 'x'} satisfies Partial<UpdateBetrokkeneConfig>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [UpdateBetrokkeneComponent]
        })
            .overrideTemplate(UpdateBetrokkeneComponent, '')
            .compileComponents();

        save$ = new Subject<void>();

        fixture = TestBed.createComponent(UpdateBetrokkeneComponent);
        component = fixture.componentInstance;

        component.save$ = save$;
        component.disabled$ = of(false);
        component.pluginId = 'openklant';
        component.prefillConfiguration$ = of(emptyConfig as UpdateBetrokkeneConfig);

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
